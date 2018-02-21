package com.hedvig.claims.web;

import com.hedvig.claims.commands.*;
import com.hedvig.claims.query.ClaimEntity;
import com.hedvig.claims.query.ClaimsRepository;
import com.hedvig.claims.query.FileUploadRepository;
import com.hedvig.claims.query.ResourceNotFoundException;
import com.hedvig.claims.web.dto.*;
import com.hedvig.claims.web.dto.ClaimDataType.DataType;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/i/claims", "/_/claims"})
public class InternalController {

    private Logger log = LoggerFactory.getLogger(InternalController.class);
    private final ClaimsRepository claimsRepository;
    private final CommandGateway commandBus;
    
    @Autowired
    public InternalController(CommandBus commandBus, ClaimsRepository repository, FileUploadRepository filerepo) {
        this.commandBus = new DefaultCommandGateway(commandBus);
        this.claimsRepository = repository;
    }
    
    @RequestMapping(path = "/claim", method = RequestMethod.POST)
    public ResponseEntity<?> initiateClaim(@RequestBody FnolDTO fnol) {
    	log.info("Claim FNOL recieved!:" + fnol.toString());
        UUID uid = UUID.randomUUID();
    	commandBus.sendAndWait(new CreateClaimCommand(fnol.getUserId(),uid.toString(), LocalDateTime.now(), fnol.getAudioURL()));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(path = "/listclaims", method = RequestMethod.GET)
    public ResponseEntity<List<ClaimDTO>> getClaimsList() {
    	log.info("Getting all claims:");
    	ArrayList<ClaimDTO> claims = new ArrayList<ClaimDTO>();
        for(ClaimEntity c : claimsRepository.findAll()){
        	claims.add(new ClaimDTO(c.id, c.userId, c.audioURL, c.registrationDate));
        }
        
        return ResponseEntity.ok(claims);
    }
    
    @RequestMapping(path = "/claim", method = RequestMethod.GET)
    public ResponseEntity<ClaimDTO> getClaim(@RequestParam String claimID) {
    	log.info("Getting claim with ID:" + claimID);
        ClaimEntity claim = claimsRepository.findById(claimID).orElseThrow(() -> new ResourceNotFoundException("Could not find claim with id:" + claimID));
        ClaimDTO cdto = new ClaimDTO(claim);
        return ResponseEntity.ok(cdto);
    }
    
    @RequestMapping(path = "/adddataitem", method = RequestMethod.POST)
    public ResponseEntity<?> addDataItem(@RequestBody DataItemDTO data) {
    	log.info("Adding data item:" + data.toString());
        UUID uid = UUID.randomUUID();     
        AddDataItemCommand command = new AddDataItemCommand(uid.toString(), data.claimID, LocalDateTime.now(), data.userId, 
        		data.type, data.name, data.title, data.received, data.value);
    	commandBus.sendAndWait(command);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
    @RequestMapping(path = "/addnote", method = RequestMethod.POST)
    public ResponseEntity<?> addNote(@RequestBody NoteDTO note) {
    	log.info("Adding claim note:" + note.toString());
        UUID uid = UUID.randomUUID();     
        AddNoteCommand command = new AddNoteCommand(uid.toString(), note.claimID, LocalDateTime.now(), note.text, note.userId, note.fileURL);
    	commandBus.sendAndWait(command);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
    @RequestMapping(path = "/addpayment", method = RequestMethod.POST)
    public ResponseEntity<?> addPayment(@RequestBody PaymentDTO payment) {
    	log.info("Adding payment note:" + payment.toString());
        UUID uid = UUID.randomUUID();     
        AddPaymentCommand command = new AddPaymentCommand(uid.toString(), payment.claimID, LocalDateTime.now(), 
        		payment.userId, payment.amount, payment.note, payment.payoutDate, payment.exGratia);
        
    	commandBus.sendAndWait(command);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(path = "/updatereserve", method = RequestMethod.POST)
    public ResponseEntity<?> updateReserve(@RequestBody ReserveDTO reserve) {
        log.info("Updating claim reserve: " + reserve.toString());

        UpdateClaimsReserveCommand command = new UpdateClaimsReserveCommand(reserve.claimID, reserve.userId,
                LocalDateTime.now(), reserve.amount);

        commandBus.sendAndWait(command);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(path = "/updatestate", method = RequestMethod.POST)
    public ResponseEntity<?> updateState(@RequestBody ClaimStateDTO state) {
        log.info("Updating claim reserve: " + state.toString());

        UpdateClaimsStateCommand command = new UpdateClaimsStateCommand(state.claimID, state.userId,
                LocalDateTime.now(), state.state);

        commandBus.sendAndWait(command);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(path = "/updatetype", method = RequestMethod.POST)
    public ResponseEntity<?> updateType(@RequestBody ClaimTypeDTO type) {
        log.info("Updating claim reserve: " + type.toString());

        UpdateClaimTypeCommand command = new UpdateClaimTypeCommand(type.claimID, type.userId,
                LocalDateTime.now(), type.type);

        commandBus.sendAndWait(command);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
    @RequestMapping(path = "claimTypes", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<ClaimType>> claimTypes() {

        ArrayList<ClaimType> claimTypes = new ArrayList<ClaimType>();
        
        ClaimDataType c11 = new ClaimDataType(DataType.DATE,"DATE","Datum");
        ClaimDataType c12 = new ClaimDataType(DataType.TEXT,"PLACE","Plats");
        ClaimDataType c13 = new ClaimDataType(DataType.ASSET,"ITEM","Pryl");
        ClaimDataType c14 = new ClaimDataType(DataType.FILE,"POLICE_REPORT","Polisanmälan");
        ClaimDataType c15 = new ClaimDataType(DataType.FILE,"RECIEPT","Kvitto");

        ClaimType ct1 = new ClaimType("THEFT","Stöld");
        ct1.addRequiredData(c11);
        ct1.addRequiredData(c12);
        ct1.addRequiredData(c13);
        ct1.addOptionalData(c14);
        ct1.addOptionalData(c15);
        
        ClaimType ct2 = new ClaimType("FILE","Brand");
        ct2.addRequiredData(c11);
        ct2.addRequiredData(c12);
        ct2.addRequiredData(c13);
        ct2.addOptionalData(c14);
        ct2.addOptionalData(c15);
        
        ClaimType ct3 = new ClaimType("DRULLE","Drulle");
        ct3.addRequiredData(c11);
        ct3.addRequiredData(c12);
        ct3.addRequiredData(c13);
        ct3.addOptionalData(c14);
        ct3.addOptionalData(c15);
        
        claimTypes.add(ct1);
        claimTypes.add(ct2);
        claimTypes.add(ct3);
        return ResponseEntity.ok(claimTypes);
    }



}
