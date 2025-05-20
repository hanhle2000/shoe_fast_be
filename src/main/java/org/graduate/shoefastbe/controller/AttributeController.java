package org.graduate.shoefastbe.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.graduate.shoefastbe.dto.attribute.AttributeDtoResponse;
import org.graduate.shoefastbe.service.attribute.AttributeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/attribute")
@AllArgsConstructor
@CrossOrigin
public class AttributeController {

    private final AttributeService attributeService;

    @GetMapping("/get-by-product")
    @Operation(summary = "Lấy chi tiết theo productId")
    AttributeDtoResponse getAttributeByProductId(@RequestParam Long productId, @RequestParam Long size ){
        return attributeService.getAttributeByProductId(productId,size);
    }
    @GetMapping()
    @Operation(summary = "Lấy chi tiết theo id")
    AttributeDtoResponse getAttributeById(@RequestParam Long id){
        return attributeService.getAttributeById(id);
    }

}
