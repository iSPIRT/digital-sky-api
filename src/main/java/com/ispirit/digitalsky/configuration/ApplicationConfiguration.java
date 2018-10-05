package com.ispirit.digitalsky.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.document.ImportDroneApplication;
import com.ispirit.digitalsky.document.LocalDroneAcquisitionApplication;
import com.ispirit.digitalsky.domain.Manufacturer;
import com.ispirit.digitalsky.repository.*;
import com.ispirit.digitalsky.repository.storage.FileSystemStorageService;
import com.ispirit.digitalsky.repository.storage.StorageService;
import com.ispirit.digitalsky.service.*;
import com.ispirit.digitalsky.service.api.*;
import com.ispirit.digitalsky.util.CustomValidator;
import com.sendgrid.SendGrid;
import freemarker.template.TemplateExceptionHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Validator;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfiguration {

    @Value("${DEFAULT_FROM_EMAIL_ID:no-reply@digitalsky.com}")
    private String defaultFromEmailId;

    @Value("${SEND_GRID_API_KEY:default}")
    private String sendGridApiKey;

    @Value("${RESET_PASSWORD_PATH:http://192.168.33.10:3000/resetPassword}")
    private String resetPasswordBasePath;

    @Value("${ACCOUNT_VERIFICATION_PATH:http://localhost:3000/verifyAccount}")
    private String accountVerificationPath;

    @Value("${JWT_KEYSTORE_PATH:classpath:keystore.jks}")
    private String jwtKeyStorePath;

    @Value("${JWT_KEYSTORE_PASSWORD:cacms789}")
    private String jwtKeyStorePassword;

    @Value("${JWT_KEYSTORE_TYPE:jks}")
    private String jwtKeyStoreType;

    @Value("${JWT_KEY_ALIAS:tomcat-localhost}")
    private String jwtKeyAlias;

    @Value("${JWT_KEY_PASSWORD:cacms789}")
    private String jwtKeyPassword;

    @Value("${JWT_EXPIRY_TIME_IN_DAYS:30}")
    private String jwtExpiryInDays;

    @Value("${FILE_STORAGE_LOCATION:uploads}")
    private String storageLocation;

    @Value("${RE_CAPTCHA_VERIFY_URL:https://www.google.com/recaptcha/api/siteverify}")
    private String reCaptchaVerifyUrl;

    @Value("${RE_CAPTCHA_SITE_SECRET:secret}")
    private String reCaptchaSiteSecret;

    @Value("${DS_CERT_PATH:classpath:cert.pem}")
    private String digitalSkyCertificatePath;

    @Value("${DS_CERT_PRIVATE_KEY_PATH:classpath:key.pem}")
    private String digitalSkyPrivateKeyPath;

    @Value("${MANUFACTURER_DIGITAL_CERT_MANUFACTURER_ATTRIBUTE_NAME:cn}")
    private String manufacturerDigitalCertManufacturerAttributeName;

    @Value("${MANUFACTURER_DIGITAL_CERT_VALIDATION_ENABLED:true}")
    private boolean manufacturerDigitalCertValidationEnabled;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return userService;
    }

    @Bean
    public UserService userService(
            UserRepository userRepository,
            EmailService emailService,
            DroneAcquisitionApplicationService<ImportDroneApplication> importDroneService,
            UAOPApplicationService uaopApplicationService,
            DroneAcquisitionApplicationService<LocalDroneAcquisitionApplication> localDroneService) {
        return new CustomUserDetailService(userRepository,
                emailService,
                localDroneService,
                importDroneService,
                uaopApplicationService,
                resetPasswordBasePath,
                accountVerificationPath);
    }

    @Bean
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public SecurityTokenService securityTokenService(ResourceLoader resourceLoader) {
        return new JwtTokenService(resourceLoader, Integer.parseInt(jwtExpiryInDays), jwtKeyStorePath, jwtKeyStorePassword, jwtKeyStoreType, jwtKeyAlias, jwtKeyPassword);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(SecurityTokenService securityTokenService, UserService userService) {
        return new JwtAuthenticationFilter(userService, securityTokenService);
    }

    @Bean
    StorageService storageService() {
        return new FileSystemStorageService(storageLocation);
    }

    @Bean
    public freemarker.template.Configuration freemarkerConfiguration() {
        freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_20);
        cfg.setClassForTemplateLoading(this.getClass(), "/templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setLocalizedLookup(false);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        return cfg;
    }

    @Bean
    EmailService sendGridEmailService(freemarker.template.Configuration freemarkerConfiguration) {
        SendGrid sendGrid = new SendGrid(sendGridApiKey);
        return new SendGridEmailService(sendGrid, freemarkerConfiguration, defaultFromEmailId);
    }

    @Bean
    PilotService pilotService(PilotRepository pilotRepository, StorageService storageService) {
        return new PilotServiceImpl(pilotRepository, storageService);
    }

    @Bean
    IndividualOperatorService individualOperatorService(IndividualOperatorRepository individualOperatorRepository, OrganizationOperatorRepository organizationOperatorRepository) {
        return new IndividualOperatorServiceImpl(individualOperatorRepository, organizationOperatorRepository);
    }

    @Bean
    OrganizationOperatorService organizationOperatorService(OrganizationOperatorRepository organizationOperatorRepository, IndividualOperatorRepository individualOperatorRepository) {
        return new OrganizationOperatorServiceImpl(organizationOperatorRepository, individualOperatorRepository);
    }

    @Bean
    ManufacturerService manufacturerService(ManufacturerRepository manufacturerRepository, StorageService storageService) {
        return new ManufacturerServiceImpl(manufacturerRepository, storageService);
    }

    @Bean
    DirectorService directorService(DirectorRepository directorRepository) {
        return new DirectorServiceImpl(directorRepository);
    }

    @Bean
    OperatorDroneService operatorDroneService(OperatorDroneRepository operatorDroneRepository, UserProfileService userProfileService) {
        return new OperatorDroneServiceImpl(operatorDroneRepository, userProfileService );
    }

    @Bean
    DroneAcquisitionApplicationService<LocalDroneAcquisitionApplication> localDroneAcquisitionService(
            LocalDroneAcquisitionApplicationRepository droneAcquisitionRepository,
            StorageService storageService,
            DroneTypeService droneService,
            OperatorDroneService operatorDroneService, UserProfileService userProfileService) {

        return new DroneAcquisitionApplicationServiceImpl<LocalDroneAcquisitionApplication>(droneAcquisitionRepository, storageService, droneService, operatorDroneService, userProfileService);
    }

    @Bean
    DroneAcquisitionApplicationService<ImportDroneApplication> importedDroneAcquisitionService(
            ImportDroneApplicationRepository droneAcquisitionRepository,
            StorageService storageService,
            DroneTypeService droneService,
            OperatorDroneService operatorDroneService,
            UserProfileService userProfileService) {

        return new DroneAcquisitionApplicationServiceImpl<ImportDroneApplication>(droneAcquisitionRepository, storageService, droneService, operatorDroneService, userProfileService);
    }

    @Bean
    UAOPApplicationService uaopApplicationService(StorageService storageService, UAOPApplicationRepository uaopApplicationRepository) {
        return new UAOPApplicationServiceImpl(uaopApplicationRepository, storageService);
    }

    @Bean
    UINApplicationService uinApplicationService(StorageService storageService,
                                                UINApplicationRepository uinApplicationRepository,
                                                OperatorDroneService operatorDroneService,
                                                UserProfileService userProfileService,
                                                DroneDeviceRepository droneDeviceRepository) {
        return new UINApplicationServiceImpl(uinApplicationRepository, storageService, operatorDroneService, userProfileService, droneDeviceRepository);
    }

    @Bean
    DroneTypeService droneTypeService(DroneTypeRepository droneTypeRepository, StorageService storageService) {
        return new DroneTypeServiceImpl(droneTypeRepository, storageService);
    }

    @Bean
    DroneDeviceService droneDeviceService(DroneDeviceRepository droneRepository, DigitalSignatureVerifierService signatureVerifierService,
                                          IndividualOperatorRepository individualOperatorRepository,
                                          OrganizationOperatorRepository organizationOperatorRepository,
                                          OperatorDroneService operatorDroneService,
                                          ManufacturerService manufacturerService) {
        return new DroneDeviceServiceImpl(droneRepository, signatureVerifierService,individualOperatorRepository, organizationOperatorRepository, operatorDroneService, manufacturerService );
    }

    @Bean
    DigitalSignatureVerifierService signatureVerifierService(ManufacturerService manufacturerService, DigitalCertificateValidatorService digitalCertificateValidatorService) {
        return new DigitalSignatureVerifierServiceImpl(digitalCertificateValidatorService, manufacturerDigitalCertManufacturerAttributeName, manufacturerDigitalCertValidationEnabled);
    }

    @Bean
    DigitalCertificateValidatorService digitalCertificateValidatorService() {
        return new DigitalCertificateValidatorServiceImpl();
    }

    @Bean
    CustomValidator customValidator(Validator validator){
        return new CustomValidator(validator);
    }

    @Bean
    ReCaptchaService reCaptchaService(RestTemplate restTemplate){
        return new GoogleReCaptchaService(restTemplate, reCaptchaVerifyUrl, reCaptchaSiteSecret);
    }

    @Bean
    BlogService blogService(BlogRepository blogRepository){
        return new BlogServiceImpl(blogRepository);
    }

    @Bean
    UserProfileService userProfileService(PilotRepository pilotRepository, IndividualOperatorRepository individualOperatorRepository, OrganizationOperatorRepository organizationOperatorRepository, ManufacturerRepository manufacturerRepository){
        return new UserProfileServiceImpl(pilotRepository, individualOperatorRepository, organizationOperatorRepository, manufacturerRepository);
    }

    @Bean
    OccurrenceReportService occurrenceReportService(OccurrenceReportRepository occurrenceReportRepository){
        return new OccurrenceReportServiceImpl(occurrenceReportRepository);
    }

    @Bean
    AirspaceCategoryService airspaceCategoryService(AirspaceCategoryRepository airspaceCategoryRepository){
        return new AirspaceCategoryServiceImpl(airspaceCategoryRepository);
    }

    @Bean
    FlyDronePermissionApplicationService flyDronePermissionApplicationService(FlyDronePermissionApplicationRepository repository, StorageService storageService, AirspaceCategoryService airspaceCategoryService, freemarker.template.Configuration freemarkerConfiguration, DigitalSignService digitalSignService, OperatorDroneService operatorDroneService, UserProfileService userProfileService, PilotService pilotService){
        return new FlyDronePermissionApplicationServiceImpl(repository, storageService, airspaceCategoryService, digitalSignService, operatorDroneService, userProfileService, pilotService, freemarkerConfiguration);
    }

    @Bean
    DigitalSignService digitalSignService(ResourceLoader resourceLoader){
        return new DigitalSignServiceImpl(resourceLoader, digitalSkyPrivateKeyPath, digitalSkyCertificatePath);
    }

    @Bean
    RestTemplate restTemplate(){
        int timeout = 25000;
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();
        CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultRequestConfig(config)
                .build();
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(client);
        return new RestTemplate(httpRequestFactory);
    }

    @Bean
    public ObjectMapper myObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return objectMapper;
    }
}
