package com.hedvig.claims.commands;

import java.time.Instant;

import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.claims.web.dto.ClaimDataType.DataType;

import lombok.Value;

@Value
public class AddDataItemCommand {

    private static Logger log = LoggerFactory.getLogger(AddDataItemCommand.class);

    public String id;

    @TargetAggregateIdentifier
    public String claimID;
    public Instant date;
    public String userId;

    public DataType type;
    public String name;
    public String title;
    public Boolean received;
    public String value;

    public AddDataItemCommand(String id,String claimID, Instant date, String userId, 
            DataType type, String name, String title, Boolean received, String value) {
        log.info("InitiateClaimCommand");
        this.id = id;
        this.claimID = claimID;
        this.date = date;
        this.userId = userId;

        this.type = type;
        this.name = name;
        this.title = title;
        this.received = received;
        this.value = value;
        log.info(this.toString());
    } 
}
