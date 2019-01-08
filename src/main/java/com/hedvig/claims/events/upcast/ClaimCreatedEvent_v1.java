package com.hedvig.claims.events.upcast;

import com.hedvig.claims.events.ClaimCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.serialization.SimpleSerializedType;
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation;
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster;
import org.dom4j.Element;

@Slf4j
public class ClaimCreatedEvent_v1 extends SingleEventUpcaster {

  private static SimpleSerializedType targetType = new SimpleSerializedType(ClaimCreatedEvent.class.getTypeName(), null);
  private static SimpleSerializedType targetTypeV1 = new SimpleSerializedType(ClaimCreatedEvent.class.getTypeName(), "1.0");

  @Override
  protected boolean canUpcast(IntermediateEventRepresentation intermediateRepresentation) {
    return intermediateRepresentation.getType().equals(targetType) || intermediateRepresentation.getType().equals(targetTypeV1);
  }

  @Override
  protected IntermediateEventRepresentation doUpcast(IntermediateEventRepresentation intermediateRepresentation) {
    return intermediateRepresentation.upcastPayload(
      new SimpleSerializedType(targetType.getName(), "1.1"),
      org.dom4j.Document.class,
      document -> {
        final Element rootElement = document.getRootElement();

        final Element regDate = rootElement.element("registrationDate");

        if (regDate != null) {
          rootElement.remove(regDate);
        }

        return document;
      }
    );
  }
}
