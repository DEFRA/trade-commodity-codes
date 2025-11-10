package uk.gov.defra.cdp.trade.demo.integration;

import static java.lang.String.format;
import static java.lang.String.join;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

public class API {

  private final String COMMODITY_CODES_RESOURCE_BASE_URL = "/commodity-codes";
  private final String COMMODITY_CATEGORY_RESOURCE_BASE_URL = "/commodity-categories";
  private final String COMMODITY_SPECIES_RESOURCE_BASE_URL = "/commodity-species";
  private final static String CERTIFICATE_TYPE_RESOURCE_BASE_URL = "/certificate-type";

  @FunctionalInterface
  interface MakeRequest<ResponseBodyType> {
    Response<ResponseBodyType> makeRequest(WebTestClient webTestClient);
  }

  static class Response<T> {
    private final ResponseSpec responseSpec;
    private final Function<ResponseSpec, FluxExchangeResult<T>> getBody;

    private Response(
        ResponseSpec responseSpec,
        Function<ResponseSpec, FluxExchangeResult<T>> getBody) {
      this.responseSpec = responseSpec;
      this.getBody = getBody;
    }

    private FluxExchangeResult<T> cachedFluxExchangeResult;

    FluxExchangeResult<T> response() {
      if (cachedFluxExchangeResult == null) {
        cachedFluxExchangeResult = getBody.apply(responseSpec);
      }
      return cachedFluxExchangeResult;
    }

    FluxExchangeResult<T> body() {
      return response();
    }
  }

  static MakeRequest<String> get(String URL) {

    return webTestClient -> new Response<>(
        webTestClient
            .get()
            .uri(URL)
            .exchange(),
        responseSpec -> responseSpec.returnResult(String.class)
    );
  }

  static <T> MakeRequest<String> post(T body, String URL) {

    return webTestClient -> new Response<>(
        webTestClient
            .post()
            .uri(URL)
            .bodyValue(body)
            .exchange(),
        responseSpec -> responseSpec.returnResult(String.class)
    );
  }

  private static Supplier<String> resourcePathSupplier(String param)  {
    return () -> format("/commodity-%s", param);
  }


  public static String getTopLevelUrl(String certificateType) {
    return format(getResourceUrl(resourcePathSupplier("codes"), "/%s/top-level"), certificateType);
  }

  public static String getByParentCodeUrl(String certificateType, String parentCode) {
    return format(
        getResourceUrl(resourcePathSupplier("codes"), "/%s/parent-code/%s"), certificateType, parentCode);
  }

  public static String getByCommodityCodeUrl(String certificateType, String commodityCode) {
    return format(
        getResourceUrl(resourcePathSupplier("codes"), "/%s/commodity-code/%s"),
        certificateType,
        commodityCode);
  }

  public static String getAllParentsUrl(String certificateType, String commodityCode) {
    return format(
        getResourceUrl(resourcePathSupplier("codes"), "/%s/all-parents/%s"), certificateType, commodityCode);
  }


  public static String getSupplementalUrl(String certificateType, String commodityCode,
      List<String> eppoCodes) {
    return format(
        getResourceUrl(resourcePathSupplier("codes"), "/%s/commodity-code/%s/supplemental-data?eppoCodes=%s"),
        certificateType,
        commodityCode, join(",", eppoCodes));
  }

  public static String getSupplementalUrlV2(String certificateType, String commodityCode,
      List<String> eppoCodes) {
    return format(
        getResourceUrl(resourcePathSupplier("codes"), "/v2/%s/commodity-code/%s/supplemental-data?eppoCodes=%s"),
        certificateType,
        commodityCode, join(",", eppoCodes));
  }

  public static String getSupplementalUrlV2UsingMappings(String certificateType) {
    return format(
        getResourceUrl(resourcePathSupplier("codes"), "/%s/supplemental-data"),
        certificateType);
  }

  public static String getSupplementalUrlForSpeciesName(String commodityCode, String speciesName) {
    return format(
        getResourceUrl(resourcePathSupplier("codes"), "/chedpp/commodity-code/%s/supplemental-data?speciesName=%s"),
        commodityCode, speciesName);
  }

  public static String getSupplementalUrlForSpeciesNameV2(String commodityCode, String speciesName) {
    return format(
        getResourceUrl(resourcePathSupplier("codes"), "/v2/chedpp/commodity-code/%s/supplemental-data?speciesName=%s"),
        commodityCode, speciesName);
  }

  public static String getSupplementalPostUrl(String certificateType, String commodityCode,
                                   List<String> eppoCodes) {
    return format(
            getResourceUrl(resourcePathSupplier("codes"), "/%s/commodity-code/%s/supplemental?eppoCodes=%s"),
            certificateType,
            commodityCode, join(",", eppoCodes));
  }

  public static String getSupplementalPostUrlV2(String certificateType, String commodityCode,
      List<String> eppoCodes) {
    return format(
        getResourceUrl(resourcePathSupplier("codes"), "/v2/%s/commodity-code/%s/supplemental?eppoCodes=%s"),
        certificateType,
        commodityCode, join(",", eppoCodes));
  }

  public static String getCommodityGroupsUrl(List<String> commodityCodes) {
    return format(getResourceUrl(resourcePathSupplier("codes"), "/groups?commodityCodes=%s"),
        join(",", commodityCodes));
  }

  public static String getCommodityCategoriesUrl(List<String> commodityCodes) {
    return format(
        getResourceUrl(resourcePathSupplier("codes"), "/chedpp/commodity-configuration?commodityCodes=%s"),
        join(",", commodityCodes)
    );
  }

  public static String getCommodityAttributesUrl(List<String> commodityCodes) {
    return format(
            getResourceUrl(resourcePathSupplier("codes"), "/chedpp/commodity-attributes?commodityCodes=%s"),
            join(",", commodityCodes)
    );
  }

  public static String getCommodityGroupUrl(List<String> commodityCodes) {
    return format(getResourceUrl(resourcePathSupplier("codes"), "/v2/group?commodityCodes=%s"),
        join(",", commodityCodes));
  }

  public static String getCommodityCategoryUrl(String certificateType, String commodityCode) {
    return format(getResourceUrl(resourcePathSupplier("categories"), "/%s-%s"),
        certificateType, commodityCode);
  }

  public static String getChedppSpeciesUrl(String commodityCode) {
    return format(getResourceUrl(resourcePathSupplier("species"), "/chedpp/%s"), commodityCode);
  }

  public static String getChedppSpeciesByIdUrl(String commodityCode, int speciesId) {
    return format(getResourceUrl(resourcePathSupplier("species"), "/chedpp/%s/species/%s"), commodityCode,
            speciesId);
  }

  public static String getChedpSpeciesUrl(String commodityCode) {
    return format(getResourceUrl(resourcePathSupplier("species"), "/chedp/%s"), commodityCode);
  }

  public static String getCertTypeUrl(List<String> commodityCodes) {
    if (commodityCodes == null || commodityCodes.isEmpty()) {
      return CERTIFICATE_TYPE_RESOURCE_BASE_URL;
    }
    return CERTIFICATE_TYPE_RESOURCE_BASE_URL
        + format("?commodityCodes=%s", join(",", commodityCodes));
  }

  public static String searchArticle72Url() {
    return "/article-72";
  }

  private static String getResourceUrl(Supplier<String> resourceRootSupplier, String path) {
    return resourceRootSupplier.get() + path;
  }
}
