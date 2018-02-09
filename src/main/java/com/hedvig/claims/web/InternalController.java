package com.hedvig.claims.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hedvig.claims.web.dto.ClaimDataType;
import com.hedvig.claims.web.dto.ClaimDataType.DataType;
import com.hedvig.claims.web.dto.ClaimType;

import java.util.ArrayList;

@RestController
@RequestMapping({"/i/claims", "/_/claims"})
public class InternalController {

    private Logger log = LoggerFactory.getLogger(InternalController.class);

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
