package uk.gov.defra.cdp.trade.demo.resource;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.defra.cdp.trade.demo.service.CertificateTypeService;

@ExtendWith(MockitoExtension.class)
class CertificateTypeResourceTest {

  private MockMvc mockMvc;

  @InjectMocks
  private CertificateTypeResource certificateTypeResource;

  @Mock
  private CertificateTypeService certificateTypeService;

  private static final String commodityCode_1 = "01";
  private static final Character certTypeCode1 = 'A';
  private static final Character certTypeCode2 = 'P';

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(certificateTypeResource).build();
    when(certificateTypeService.getAllCertTypes(anyList())).thenReturn(
        Map.of(commodityCode_1, List.of(certTypeCode1, certTypeCode2))
    );
  }

  @Test
  void call_getCertTypes_forValidCommodityCodesAsQueryParam_thenReturnJsonArray()
      throws Exception {
    when(certificateTypeService.getAllCertTypes(anyList())).thenReturn(
        Map.of(commodityCode_1, List.of(certTypeCode1, certTypeCode2))
    );

    mockMvc.perform(get("/certificate-type?commodityCodes="+commodityCode_1)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("{\"01\":[\"A\",\"P\"]}"));
  }

}
