package com.hedvig.generic.mustrename.web;

import com.hedvig.generic.mustrename.commands.InitiateClaimCommand;
import com.hedvig.generic.mustrename.commands.InitiateClaimForAssetCommand;
import com.hedvig.generic.mustrename.commands.DeleteClaimCommand;
import com.hedvig.generic.mustrename.commands.UpdateClaimCommand;
import com.hedvig.generic.mustrename.query.ClaimsRepository;
import com.hedvig.generic.mustrename.query.FileUploadRepository;
import com.hedvig.generic.mustrename.query.UploadFile;
import com.hedvig.generic.mustrename.web.dto.ClaimDTO;

import org.apache.commons.compress.utils.IOUtils;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class ClaimsController {

	private static Logger log = LoggerFactory.getLogger(ClaimsController.class);
    private final ClaimsRepository claimsRepository;
    private final CommandGateway commandBus;
    private final FileUploadRepository filerepo;

    @Autowired
    public ClaimsController(CommandBus commandBus, ClaimsRepository repository, FileUploadRepository filerepo) {
        this.commandBus = new DefaultCommandGateway(commandBus);
        this.claimsRepository = repository;
        this.filerepo = filerepo;
    }

    @RequestMapping(value = "/claim/fileupload/{claims_id}/{meta_info}", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
    public String handleFileUpload(@ModelAttribute("file") MultipartFile fileUpload, @PathVariable String meta_info, @PathVariable UUID claims_id,
    		@RequestHeader(value="hedvig.token", required = false) String hid) throws Exception {
    		UUID uid = UUID.randomUUID();
            log.info("Saving file: " + fileUpload.getOriginalFilename());

            UploadFile uploadFile = new UploadFile();
            uploadFile.setFileName(fileUpload.getOriginalFilename());
            uploadFile.setData(fileUpload.getBytes());
            uploadFile.setUserId(hid);
            uploadFile.setImageId(uid);
            uploadFile.setMetaInfo(meta_info);
            uploadFile.setClaimsId(claims_id);
            uploadFile.setSize(fileUpload.getSize());
            uploadFile.setContentType(fileUpload.getContentType());
            filerepo.save(uploadFile);             

        return "{id:"+uid+"}";
    } 
    
    @RequestMapping(value = "/claim/file/{file_id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImageAsResponseEntity(@PathVariable UUID file_id, 
    		@RequestHeader(value="hedvig.token", required = false) String hid) {
        HttpHeaders headers = new HttpHeaders();
        
        Optional<UploadFile> file = filerepo.findByImageId(file_id);
        if(file.isPresent()){
            InputStream in = new ByteArrayInputStream(file.get().getData());
            byte[] media;
			try {
				media = IOUtils.toByteArray(in);
				log.info("Get image:" + file_id + " content-type:" + file.get().getContentType());
				
				headers.setContentType(MediaType.valueOf(file.get().getContentType()));
	            headers.setCacheControl(CacheControl.noCache().getHeaderValue());
	             
	            return new ResponseEntity<>(media, headers, HttpStatus.OK);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        else{
        	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @RequestMapping(path = "/claim/", method = RequestMethod.POST)
    public ResponseEntity<?> initiateClaim(@RequestHeader(value="hedvig.token", required = false) String hid) {
    	
        UUID uid = UUID.randomUUID();
        log.info("Initiate claims with id: " + uid.toString());
        commandBus.sendAndWait(new InitiateClaimCommand(hid, uid.toString(), LocalDate.now()));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(path = "/claim/asset/{asset_id}", method = RequestMethod.POST)
    public ResponseEntity<?> initiateAssetClaim(@PathVariable UUID asset_id, @RequestHeader(value="hedvig.token", required = false) String hid) {
    	
        UUID uid = UUID.randomUUID();
        log.info("Initiate claims for asset:"+ asset_id +" with new claims id: " + uid.toString());
        commandBus.sendAndWait(new InitiateClaimForAssetCommand(hid, uid.toString(), asset_id, LocalDate.now()));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}