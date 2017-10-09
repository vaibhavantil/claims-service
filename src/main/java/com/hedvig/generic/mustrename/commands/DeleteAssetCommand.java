package com.hedvig.generic.mustrename.commands;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.generic.mustrename.query.AssetEventListener;

import java.time.LocalDate;

@Value
public class DeleteAssetCommand {

	private static Logger log = LoggerFactory.getLogger(DeleteAssetCommand.class);
	
    @TargetAggregateIdentifier
    public String id;
    private String name;
    private LocalDate registrationDate;

    public DeleteAssetCommand(String id, String name, LocalDate registrationDate) {
        log.info("DeleteAssetCommand");
        this.id = id;
        this.name = name;
        this.registrationDate = registrationDate;
        log.info(this.toString());
    } 
}
