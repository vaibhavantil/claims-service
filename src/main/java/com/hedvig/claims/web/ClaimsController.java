package com.hedvig.claims.web;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hedvig.claims.audio.FfmpegClient;
import com.hedvig.claims.audio.NuanceClient;
import com.hedvig.claims.query.ClaimsRepository;
import com.hedvig.claims.query.FileUploadRepository;
import com.hedvig.claims.query.UploadFile;

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

    @RequestMapping(value = "/claim/fileupload/", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
    public String handleFileUpload(@ModelAttribute("file") Optional<MultipartFile> fileUpload, @PathVariable Optional<String> meta_info, //@PathVariable UUID claims_id,
    		@RequestHeader(value="hedvig.token", required = false) String hid) throws Exception {
    		
    	// File database id
    	UUID uid = UUID.randomUUID();
    	UUID claims_id = UUID.randomUUID();
    		if(fileUpload.isPresent()){
    			storeFile(fileUpload.get(), hid, uid, meta_info.isPresent()?meta_info.get():"", claims_id);
    			
    			String destination = "C:\\Users\\John\\Documents\\" + fileUpload.get().getOriginalFilename();
    			File file = new File(destination);
    			fileUpload.get().transferTo(file);
    			
    	        NuanceClient nc = new NuanceClient();
    	        FfmpegClient fc = new FfmpegClient();
    	        String pcmFile = fc.convertToPCM(destination);
    	        nc.runDictation("C:\\Users\\John\\Documents\\test.pcm");
    		}
            
        return "{id:"+uid+"}";
    } 
    
    private void storeFile(MultipartFile fileUpload, String hid, UUID uid, String meta_info, UUID claims_id) throws IOException{
        log.info("Storing file: " + fileUpload.getOriginalFilename());
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

}