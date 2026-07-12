package com.gossamercms;

import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static final ObjectMapper OBJECT_MAPPER =
            JsonMapper.builder()
                    .addModule(new JavaTimeModule())
                    .build();
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        List<PatientAddressDto> addresses =
                OBJECT_MAPPER.readValue(
                        json,
                        OBJECT_MAPPER.getTypeFactory()
                                .constructCollectionType(List.class, PatientAddressDto.class)
                );
    }
}