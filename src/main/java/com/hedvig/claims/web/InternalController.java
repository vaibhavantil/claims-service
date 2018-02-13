package com.hedvig.claims.web;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.callbacks.LoggingCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hedvig.claims.commands.AddDataItemCommand;
import com.hedvig.claims.commands.AddNoteCommand;
import com.hedvig.claims.commands.AddPaymentCommand;
import com.hedvig.claims.commands.CreateClaimCommand;
import com.hedvig.claims.commands.UpdateClaimsStateCommand;
import com.hedvig.claims.events.NoteAddedEvent;
import com.hedvig.claims.query.ClaimEntity;
import com.hedvig.claims.query.ClaimsRepository;
import com.hedvig.claims.query.FileUploadRepository;
import com.hedvig.claims.query.ResourceNotFoundException;
import com.hedvig.claims.web.dto.FnolDTO;
import com.hedvig.claims.web.dto.NoteDTO;
import com.hedvig.claims.web.dto.PaymentDTO;
import com.hedvig.claims.web.dto.ClaimDTO;
import com.hedvig.claims.web.dto.ClaimDataDTO;
import com.hedvig.claims.web.dto.ClaimDataType;
import com.hedvig.claims.web.dto.ClaimDataType.DataType;
import com.hedvig.claims.web.dto.ClaimType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
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

    @RequestMapping(path = "/claim", method = RequestMethod.GET)
    public ResponseEntity<?> getClaim(@RequestParam String claimID) {
    	log.info("Getting claim with ID:" + claimID);
        ClaimEntity claim = claimsRepository.findById(claimID).orElseThrow(() -> new ResourceNotFoundException("Could not find claim with id:" + claimID));
        
        return ResponseEntity.ok(claim);
    }
    
    @RequestMapping(path = "/adddataitem", method = RequestMethod.POST)
    public ResponseEntity<?> addDataItem(@RequestBody ClaimDataDTO data) {
    	log.info("Adding data item:" + data.toString());
        UUID uid = UUID.randomUUID();     
        AddDataItemCommand command = new AddDataItemCommand(uid.toString(), data.claimID, LocalDateTime.now(), data.userId, 
        		data.type, data.name, data.title, data.recieved);
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
