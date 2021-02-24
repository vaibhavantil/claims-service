package com.hedvig.claims.aggregates;

import static com.hedvig.claims.util.TzHelper.SWEDEN_TZ;
import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;
import com.hedvig.claims.commands.AddAutomaticPaymentCommand;
import com.hedvig.claims.commands.AddDataItemCommand;
import com.hedvig.claims.commands.AddExpensePaymentCommand;
import com.hedvig.claims.commands.AddFailedAutomaticPaymentCommand;
import com.hedvig.claims.commands.AddIndemnityCostPaymentCommand;
import com.hedvig.claims.commands.AddInitiatedAutomaticPaymentCommand;
import com.hedvig.claims.commands.AddNoteCommand;
import com.hedvig.claims.commands.AddPaymentCommand;
import com.hedvig.claims.commands.CreateBackofficeClaimCommand;
import com.hedvig.claims.commands.CreateClaimCommand;
import com.hedvig.claims.commands.MarkClaimFileAsDeletedCommand;
import com.hedvig.claims.commands.SetClaimFileCategoryCommand;
import com.hedvig.claims.commands.SetContractForClaimCommand;
import com.hedvig.claims.commands.TranscribeAudioCommand;
import com.hedvig.claims.commands.UpdateClaimTypeCommand;
import com.hedvig.claims.commands.UpdateClaimsReserveCommand;
import com.hedvig.claims.commands.UpdateClaimsStateCommand;
import com.hedvig.claims.commands.UpdateEmployeeClaimStatusCommand;
import com.hedvig.claims.commands.UploadClaimFileCommand;
import com.hedvig.claims.events.AudioTranscribedEvent;
import com.hedvig.claims.events.AutomaticPaymentAddedEvent;
import com.hedvig.claims.events.AutomaticPaymentFailedEvent;
import com.hedvig.claims.events.AutomaticPaymentInitiatedEvent;
import com.hedvig.claims.events.BackofficeClaimCreatedEvent;
import com.hedvig.claims.events.ClaimCreatedEvent;
import com.hedvig.claims.events.ClaimFileCategorySetEvent;
import com.hedvig.claims.events.ClaimFileMarkedAsDeletedEvent;
import com.hedvig.claims.events.ClaimFileUploadedEvent;
import com.hedvig.claims.events.ClaimStatusUpdatedEvent;
import com.hedvig.claims.events.ClaimsReserveUpdateEvent;
import com.hedvig.claims.events.ClaimsTypeUpdateEvent;
import com.hedvig.claims.events.ContractSetForClaimEvent;
import com.hedvig.claims.events.DataItemAddedEvent;
import com.hedvig.claims.events.EmployeeClaimStatusUpdatedEvent;
import com.hedvig.claims.events.ExpensePaymentAddedEvent;
import com.hedvig.claims.events.IndemnityCostPaymentAddedEvent;
import com.hedvig.claims.events.NoteAddedEvent;
import com.hedvig.claims.events.PaymentAddedEvent;
import com.hedvig.claims.query.Carrier;
import com.hedvig.claims.query.ClaimFile;
import com.hedvig.claims.web.dto.PaymentType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.jetbrains.annotations.NotNull;

@Aggregate
@Slf4j
public class ClaimsAggregate {

    public enum ClaimStates {
        OPEN,
        CLOSED,
        REOPENED
    }

    @AggregateIdentifier
    public String id;

    public String userId;
    public String audioURL;
    public Instant registrationDate;
    public ClaimStates state;
    public Double reserve;
    public String type;
    public ClaimSource claimSource;

    public ArrayList<DataItem> data;
    public HashMap<String, Payment> payments;
    public ArrayList<Note> notes;
    public ArrayList<String> assets;

    public ArrayList<ClaimFile> claimFiles;
    public boolean isCoveringEmployee;

    @NotNull
    public AudioTranscription transcriptionResult;
    public UUID contractId;

    public ClaimsAggregate() {
        log.info("Instantiate ClaimsAggregate");
    }

    @CommandHandler
    public ClaimsAggregate(CreateClaimCommand command) {
        log.info("create claim");
        apply(
            new ClaimCreatedEvent(
                command.getId(),
                command.getUserId(),
                command.getAudioURL(),
                command.getContactId()
            )
        );
    }

    @CommandHandler
    public ClaimsAggregate(CreateBackofficeClaimCommand command) {
        log.info("create claim");
        apply(
            new BackofficeClaimCreatedEvent(
                command.getId(),
                command.getMemberId(),
                command.getRegistrationDate(),
                command.getClaimSource(),
                command.getContractId()
            )
        );
    }

    @CommandHandler
    public void update(UpdateClaimsStateCommand command) {
        log.info("update claim state");
        apply(
            new ClaimStatusUpdatedEvent(
                command.getClaimsId(),
                this.userId,
                command.getRegistrationDate(),
                command.getState()));

        if (command.getState() == ClaimStates.CLOSED) {
            apply(
                new ClaimsReserveUpdateEvent(
                    command.getClaimsId(),
                    LocalDateTime.now(),
                    this.userId,
                    0.0));
        }
    }

    @CommandHandler
    public void updateReserve(UpdateClaimsReserveCommand command) {
        log.info("update claim reserve");
        apply(
            new ClaimsReserveUpdateEvent(
                command.getClaimsId(),
                command.getRegistrationDate(),
                command.getUserId(),
                command.getAmount()));
    }

    @CommandHandler
    public void updateType(UpdateClaimTypeCommand command) {
        log.info("update claim type");
        apply(
            new ClaimsTypeUpdateEvent(
                command.getClaimsId(),
                command.getRegistrationDate(),
                this.userId,
                command.getType()));
    }

    @CommandHandler
    public void addDataItem(AddDataItemCommand command) {
        log.info("adding data item to claim");
        DataItemAddedEvent ne = new DataItemAddedEvent(
            command.getId(),
            command.getClaimID(),
            command.getDate(),
            this.userId,
            command.getType(),
            command.getName(),
            command.getTitle(),
            command.getReceived(),
            command.getValue()
        );
        apply(ne);
    }

    @CommandHandler
    public void handle(AddNoteCommand command) {
        log.info("adding note to claim");
        NoteAddedEvent ne = new NoteAddedEvent();
        ne.setClaimsId(command.getClaimID());
        ne.setDate(command.getDate());
        ne.setFileURL(command.getFileURL());
        ne.setId(command.getId());
        ne.setText(command.getText());
        ne.setUserId(this.userId);
        apply(ne);
    }

    @CommandHandler
    public void handle(AddPaymentCommand command) {
        ensureSamePaymentCarrierOrThrow(command.getCarrier());
        PaymentAddedEvent pe = new PaymentAddedEvent(
            command.getId(),
            command.getClaimID(),
            this.userId,
            command.getAmount(),
            command.getDeductible(),
            command.getNote(),
            command.getExGratia(),
            command.getHandlerReference(),
            command.getCarrier()
        );
        apply(pe);
    }

    @CommandHandler
    public void handle(AddIndemnityCostPaymentCommand command) {
        ensureSamePaymentCarrierOrThrow(command.getCarrier());
        apply(new IndemnityCostPaymentAddedEvent(
            command.getId(),
            command.getClaimId(),
            this.userId,
            command.getAmount(),
            command.getDeductible(),
            command.getNote(),
            command.getExGratia(),
            command.getHandlerReference(),
            command.getCarrier()
        ));
    }

    @CommandHandler
    public void handle(AddExpensePaymentCommand command) {
        ensureSamePaymentCarrierOrThrow(command.getCarrier());
        apply(new ExpensePaymentAddedEvent(
            command.getId(),
            command.getClaimId(),
            this.userId,
            command.getAmount(),
            command.getDeductible(),
            command.getNote(),
            command.getExGratia(),
            command.getHandlerReference(),
            command.getCarrier()
        ));
    }

    @CommandHandler
    public void handle(AddAutomaticPaymentCommand command) {
        ensureSamePaymentCarrierOrThrow(command.getCarrier());
        apply(new AutomaticPaymentAddedEvent(
            UUID.randomUUID().toString(),
            command.getClaimId(),
            this.userId,
            command.getAmount(),
            command.getDeductible(),
            command.getNote(),
            command.isExGracia(),
            command.getHandlerReference(),
            command.getSanctionCheckSkipped(),
            command.getCarrier()
        ));
    }

    @CommandHandler
    public void addInitiatedAutomaticPayment(AddInitiatedAutomaticPaymentCommand command) {
        log.info("add initiated automatic payment to member {} for claim {}", command.getMemberId(),
            command.getClaimId());

        AutomaticPaymentInitiatedEvent e = new AutomaticPaymentInitiatedEvent(
            command.getId(),
            command.getClaimId(),
            this.userId,
            command.getTransactionReference(),
            command.getTransactionStatus());

        apply(e);
    }

    @CommandHandler
    public void addFailedAutomaticPayment(AddFailedAutomaticPaymentCommand command) {
        log.info("payment failed to be processed to member {} for claim {}", command.getMemberId(), command.getClaimId());

        apply(new AutomaticPaymentFailedEvent(
            command.getId(),
            command.getClaimId(),
            this.userId,
            command.getTransactionStatus()
        ));
    }

    @CommandHandler
    public void setContractForClaim(SetContractForClaimCommand cmd) {
        if (cmd.getContractId().equals(this.contractId)) {
            return;
        }
        log.info("setting contract contractId: {} to claim {} for member {}",
            cmd.getContractId(),
            cmd.getClaimId(),
            cmd.getMemberId()
        );

        apply(new ContractSetForClaimEvent(
            this.id,
            cmd.getContractId(),
            this.userId
        ));
    }

    @CommandHandler
    public void on(UpdateEmployeeClaimStatusCommand command) {
        apply(new EmployeeClaimStatusUpdatedEvent(command.getClaimId(), command.isCoveringEmployee()));
    }

    @CommandHandler
    public void on(UploadClaimFileCommand command) {
        log.info("add claim file to claim {}", command.getClaimId());

        apply(new ClaimFileUploadedEvent(
            command.getClaimFileId(),
            command.getBucket(),
            command.getKey(),
            command.getClaimId(),
            command.getContentType(),
            command.getUploadedAt(),
            command.getFileName(),
            command.getUploadSource()
        ));
    }

    @CommandHandler
    public void on(MarkClaimFileAsDeletedCommand command) {
        Optional<ClaimFile> claimFileMaybe = claimFiles.stream()
            .filter(claimFile -> claimFile.getId().equals(command.getClaimFileId()))
            .findAny();

        if (!claimFileMaybe.isPresent()) {
            throw new RuntimeException(
                "Cannot find claim file with an id of " + command.getClaimFileId());
        }

        if (claimFileMaybe.get().getMarkedAsDeleted()) {
            throw new RuntimeException(
                "Cannot delete claim file " + claimFileMaybe.get().getId() + " it has already been marked as deleted"
            );
        }
        apply(new ClaimFileMarkedAsDeletedEvent(
            command.getClaimFileId(),
            command.getClaimId(),
            command.getDeletedBy(),
            Instant.now())
        );
    }


    @CommandHandler
    public void on(SetClaimFileCategoryCommand command) {
        apply(new ClaimFileCategorySetEvent(command.getClaimFileId(), command.getClaimId(), command.getCategory()));
    }

    @CommandHandler
    public void on(TranscribeAudioCommand command) {
        apply(new AudioTranscribedEvent(this.id, command.getText(), command.getConfidence(), command.getLanguageCode()));
    }

    // ----------------- Event sourcing --------------------- //

    @EventSourcingHandler
    public void on(ClaimCreatedEvent event, @Timestamp Instant timestamp) {
        this.id = event.getId();
        this.userId = event.getUserId();
        this.registrationDate = timestamp;
        this.audioURL = event.getAudioURL();

        // Init data structures
        this.notes = new ArrayList<Note>();
        this.payments = new HashMap<>();
        this.assets = new ArrayList<String>();
        this.data = new ArrayList<>();
        this.claimSource = ClaimSource.APP;
        this.claimFiles = new ArrayList<>();

        this.isCoveringEmployee = false;
        this.contractId = event.getContractId();
    }

    @EventSourcingHandler
    public void on(BackofficeClaimCreatedEvent event) {
        this.id = event.getId();
        this.userId = event.getMemberId();
        this.registrationDate = event.getRegistrationDate();
        this.claimSource = event.getClaimSource();

        // Init data structures
        this.notes = new ArrayList<Note>();
        this.payments = new HashMap<>();
        this.assets = new ArrayList<String>();
        this.data = new ArrayList<>();
        this.claimFiles = new ArrayList<>();

        this.isCoveringEmployee = false;
        this.contractId = event.getContractId();
    }

    @EventSourcingHandler
    public void on(ClaimStatusUpdatedEvent event) {
        this.state = event.getState();
    }

    @EventSourcingHandler
    public void on(ClaimsReserveUpdateEvent event) {
        this.reserve = event.amount;
    }

    @EventSourcingHandler
    public void on(ClaimsTypeUpdateEvent event) {
        this.type = event.type;
    }

    @EventSourcingHandler
    public void on(DataItemAddedEvent event) {
        DataItem dataItem = new DataItem();
        dataItem.id = event.getId();
        dataItem.date = event.getDate();
        dataItem.userId = event.getUserId();
        dataItem.name = event.getName();
        dataItem.received = event.getReceived();
        dataItem.title = event.getTitle();
        dataItem.type = event.getType();
        dataItem.value = event.getValue();
        data.add(dataItem);
    }

    @EventSourcingHandler
    public void on(PaymentAddedEvent event) {
        Payment payment = new Payment();
        payment.id = event.getId();
        payment.userId = event.getUserId();
        payment.amount = event.getAmount();
        payment.deductible = event.getDeductible();
        payment.note = event.getNote();
        payment.exGratia = event.getExGratia();
        payment.type = PaymentType.Manual;
        payment.handlerReference = event.getHandlerReference();
        payment.payoutStatus = PayoutStatus.COMPLETED;
        payment.carrier = event.getCarrier();
        payments.put(event.getId(), payment);
    }

    @EventSourcingHandler
    public void on(AutomaticPaymentAddedEvent event, @Timestamp Instant timestamp) {
        Payment payment = new Payment();
        payment.id = event.getId();
        payment.date = LocalDateTime.ofInstant(timestamp, SWEDEN_TZ);
        payment.userId = event.getMemberId();
        payment.amount = event.getAmount().getNumber().doubleValueExact();
        payment.deductible = event.getDeductible().getNumber().doubleValueExact();
        payment.payoutDate = LocalDateTime.ofInstant(timestamp, SWEDEN_TZ);
        payment.note = event.getNote();
        payment.exGratia = event.isExGracia();
        payment.type = PaymentType.Automatic;
        payment.handlerReference = event.getHandlerReference();
        payment.payoutStatus = PayoutStatus.PREPARED;
        payment.carrier = event.getCarrier();
        payments.put(event.getId(), payment);
    }

    @EventSourcingHandler
    public void on(IndemnityCostPaymentAddedEvent event) {
        Payment payment = new Payment();
        payment.id = event.getId();
        payment.amount = event.getAmount().getNumber().doubleValue();
        payment.deductible = event.getDeductible().getNumber().doubleValue();
        payment.payoutDate = null;
        payment.note = event.getNote();
        payment.exGratia = event.getExGratia();
        payment.type = PaymentType.IndemnityCost;
        payment.handlerReference = event.getHandlerReference();
        payment.payoutStatus = PayoutStatus.COMPLETED;
        payment.carrier = event.getCarrier();
        payments.put(event.getId(), payment);
    }

    @EventSourcingHandler
    public void on(ExpensePaymentAddedEvent event) {
        Payment payment = new Payment();
        payment.id = event.getId();
        payment.amount = event.getAmount().getNumber().doubleValue();
        payment.deductible = event.getDeductible().getNumber().doubleValue();
        payment.payoutDate = null;
        payment.note = event.getNote();
        payment.exGratia = event.getExGratia();
        payment.type = PaymentType.Expense;
        payment.handlerReference = event.getHandlerReference();
        payment.payoutStatus = PayoutStatus.COMPLETED;
        payment.carrier = event.getCarrier();
        payments.put(event.getId(), payment);
    }

    @EventSourcingHandler
    public void on(AutomaticPaymentInitiatedEvent event, @Timestamp Instant timestamp) {
        if (!payments.containsKey(event.getId())) {
            log.error("AutomaticPaymentInitiatedEvent - Cannot find payment with id {} for claim {}",
                event.getId(),
                event.getClaimId()
            );
        } else {
            Payment payment = payments.get(event.getId());
            payment.date = LocalDateTime.ofInstant(timestamp, SWEDEN_TZ);
            payment.payoutReference = event.getTransactionReference();
            payment.payoutStatus = PayoutStatus.INITIATED;
            payments.put(event.getId(), payment);
        }
    }

    @EventSourcingHandler
    public void on(AutomaticPaymentFailedEvent e, @Timestamp Instant timestamp) {
        if (!payments.containsKey(e.getId())) {
            log.error("AutomaticPaymentFailedEvent - Cannot find payment with id {} for claim {}",
                e.getId(),
                e.getClaimId()
            );
        } else {
            Payment payment = payments.get(e.getId());
            payment.date = LocalDateTime.ofInstant(timestamp, SWEDEN_TZ);
            payment.payoutStatus = PayoutStatus.parseToPayoutStatus(e.getTransactionStatus());
            payments.put(e.getId(), payment);
        }
    }

    @EventSourcingHandler
    public void on(NoteAddedEvent event) {
        Note note = new Note();
        note.id = event.getId();
        note.fileURL = event.getFileURL();
        note.text = event.getText();
        note.userId = event.getUserId();
        note.date = event.getDate();
        notes.add(note);
    }

    @EventSourcingHandler
    public void on(EmployeeClaimStatusUpdatedEvent event) {
        this.isCoveringEmployee = event.isCoveringEmployee();
    }

    @EventSourcingHandler
    public void on(ClaimFileUploadedEvent event) {
        val claimFile = new ClaimFile();
        claimFile.setId(event.getClaimFileId());
        claimFile.setBucket(event.getBucket());
        claimFile.setKey(event.getKey());
        claimFile.setContentType(event.getContentType());
        claimFile.setFileName(event.getFileName());
        claimFile.setUploadedAt(event.getUploadedAt());
        claimFile.setUploadSource(event.getUploadSource());
        claimFiles.add(claimFile);
    }

    @EventSourcingHandler
    public void on(ClaimFileMarkedAsDeletedEvent event) {
        Optional<ClaimFile> claimFileMaybe = claimFiles.stream()
            .filter(claimFile -> claimFile.getId().equals(event.getClaimFileId()))
            .findAny();

        claimFileMaybe.ifPresent(claimFile -> claimFile.setMarkedAsDeleted(true));
    }

    @EventSourcingHandler
    public void on(ClaimFileCategorySetEvent event) {
        Optional<ClaimFile> claimFileMaybe = claimFiles.stream()
            .filter(claimFile -> claimFile.getId().equals(event.getClaimFileId()))
            .findAny();

        claimFileMaybe.ifPresent(claimFile -> claimFile.setCategory(event.getCategory()));
    }

    @EventSourcingHandler
    public void on(AudioTranscribedEvent event) {
        this.transcriptionResult = new AudioTranscription(event.getText(), event.getConfidence(), event.getLanguageCode());
    }

    @EventSourcingHandler
    public void on(ContractSetForClaimEvent event) {
        this.contractId = event.getContractId();
    }


    private void ensureSamePaymentCarrierOrThrow(Carrier carrier) {
        if (this.payments.isEmpty()){
            return;
        }
        if (this.payments.values().stream().anyMatch((payment -> payment.carrier != carrier))) {
            throw new IllegalArgumentException(String.format("Invalid carrier %s (claimId=%s)", carrier, this.id));
        }
    }
}
