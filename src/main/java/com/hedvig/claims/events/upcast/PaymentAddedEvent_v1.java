package com.hedvig.claims.events.upcast;

import com.hedvig.claims.events.PaymentAddedEvent;
import lombok.Value;
import org.axonframework.serialization.SimpleSerializedType;
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation;
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster;
import org.dom4j.Element;

@Value
public class PaymentAddedEvent_v1 extends SingleEventUpcaster {

  private static SimpleSerializedType targetType = new SimpleSerializedType(PaymentAddedEvent.class.getTypeName(), null);

  @Override
  protected boolean canUpcast(IntermediateEventRepresentation intermediateRepresentation) {
    return intermediateRepresentation.getType().equals(targetType);
  }

  @Override
  protected IntermediateEventRepresentation doUpcast(IntermediateEventRepresentation intermediateRepresentation) {
    return intermediateRepresentation.upcastPayload(
      new SimpleSerializedType(targetType.getName(), "1.0"),
      org.dom4j.Document.class,
      document -> {
        final Element rootElement = document.getRootElement();

        final Element regDeductible = rootElement.element("deductible");
        regDeductible.setData((double) 1500);

        return document;
      }
    );
  }
}
