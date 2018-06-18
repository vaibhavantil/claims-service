package com.hedvig.claims.web;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hedvig.claims.aggregates.ClaimsAggregate;
import com.hedvig.claims.commands.AddDataItemCommand;
import com.hedvig.claims.commands.AddNoteCommand;
import com.hedvig.claims.commands.AddPaymentCommand;
import com.hedvig.claims.commands.CreateClaimCommand;
import com.hedvig.claims.commands.UpdateClaimTypeCommand;
import com.hedvig.claims.commands.UpdateClaimsReserveCommand;
import com.hedvig.claims.commands.UpdateClaimsStateCommand;
import com.hedvig.claims.query.ClaimEntity;
import com.hedvig.claims.query.ClaimsRepository;
import com.hedvig.claims.query.FileUploadRepository;
import com.hedvig.claims.query.ResourceNotFoundException;
import com.hedvig.claims.web.dto.ClaimDTO;
import com.hedvig.claims.web.dto.ClaimDataType;
import com.hedvig.claims.web.dto.ClaimDataType.DataType;
import com.hedvig.claims.web.dto.ClaimStateDTO;
import com.hedvig.claims.web.dto.ClaimType;
import com.hedvig.claims.web.dto.ClaimTypeDTO;
import com.hedvig.claims.web.dto.DataItemDTO;
import com.hedvig.claims.web.dto.NoteDTO;
import com.hedvig.claims.web.dto.PaymentDTO;
import com.hedvig.claims.web.dto.ReserveDTO;
import com.hedvig.claims.web.dto.StartClaimAudioDTO;

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
    
    @RequestMapping(path = "/startClaimFromAudio", method = RequestMethod.POST)
    public ResponseEntity<?> initiateClaim(@RequestBody StartClaimAudioDTO requestData) {
        log.info("Claim recieved!:" + requestData.toString());
        UUID uid = UUID.randomUUID();
        commandBus.sendAndWait(new CreateClaimCommand(uid.toString(), requestData.getUserId(), Instant.now(), requestData.getAudioURL()));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(path = "/listclaims", method = RequestMethod.GET)
    public ResponseEntity<List<ClaimDTO>> getClaimsList() {
        log.info("Getting all claims:");
        ArrayList<ClaimDTO> claims = new ArrayList<ClaimDTO>();
        for(ClaimEntity c : claimsRepository.findAll()){
            claims.add(new ClaimDTO(c.id, c.userId, c.state, c.reserve, c.type, c.audioURL, c.registrationDate));
        }
        
        return ResponseEntity.ok(claims);
    }

    @RequestMapping(path = "/listclaims/{userId}", method = RequestMethod.GET)
    public List<ClaimDTO> getClaimsByUserId(@PathVariable String userId) {
        log.info("Getting claims for: {}", userId);
        return claimsRepository
                .findByUserId(userId)
                .stream()
                .map(c -> new ClaimDTO(c.id, c.userId, c.state, c.reserve, c.type, c.audioURL, c.registrationDate))
                .collect(Collectors.toList());
    }

    @RequestMapping(path = "/stat", method = RequestMethod.GET)
    public Map<String,Long> getClaimsStatisticsByState() {
        Map<String, Long> statistics = new HashMap<>();
        for (ClaimsAggregate.ClaimStates state : ClaimsAggregate.ClaimStates.values()) {
            statistics.put(state.name(), claimsRepository.countByState(state.name()));
        }

        return statistics;
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
        AddDataItemCommand command = new AddDataItemCommand(uid.toString(), data.claimID, Instant.now(), data.userId, 
                data.type, data.name, data.title, data.received, data.value);
        commandBus.sendAndWait(command);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
    @RequestMapping(path = "/addnote", method = RequestMethod.POST)
    public ResponseEntity<?> addNote(@RequestBody NoteDTO note) {
        log.info("Adding claim note:" + note.toString());
        UUID uid = UUID.randomUUID();     
        AddNoteCommand command = new AddNoteCommand(uid.toString(), note.claimID, Instant.now(), note.text, note.userId, note.fileURL);
        commandBus.sendAndWait(command);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
    @RequestMapping(path = "/addpayment", method = RequestMethod.POST)
    public ResponseEntity<?> addPayment(@RequestBody PaymentDTO payment) {
        log.info("Adding payment note:" + payment.toString());
        UUID uid = UUID.randomUUID();     
        AddPaymentCommand command = new AddPaymentCommand(uid.toString(), payment.claimID, Instant.now(), 
                payment.userId, payment.amount, payment.note, payment.payoutDate, payment.exGratia);
        
        commandBus.sendAndWait(command);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(path = "/updatereserve", method = RequestMethod.POST)
    public ResponseEntity<?> updateReserve(@RequestBody ReserveDTO reserve) {
        log.info("Updating claim reserve: " + reserve.toString());

        UpdateClaimsReserveCommand command = new UpdateClaimsReserveCommand(reserve.claimID, reserve.userId,
                Instant.now(), reserve.amount);

        commandBus.sendAndWait(command);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(path = "/updatestate", method = RequestMethod.POST)
    public ResponseEntity<?> updateState(@RequestBody ClaimStateDTO state) {
        log.info("Updating claim reserve: " + state.toString());

        UpdateClaimsStateCommand command = new UpdateClaimsStateCommand(state.claimID, state.userId,
                Instant.now(), state.state);

        commandBus.sendAndWait(command);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(path = "/updatetype", method = RequestMethod.POST)
    public ResponseEntity<?> updateType(@RequestBody ClaimTypeDTO type) {
        log.info("Updating claim reserve: " + type.toString());

        UpdateClaimTypeCommand command = new UpdateClaimTypeCommand(type.claimID, type.userId,
                Instant.now(), type.type);

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
