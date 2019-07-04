package com.ispirit.digitalsky.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.document.ImportDroneApplication;
import com.ispirit.digitalsky.document.LocalDroneAcquisitionApplication;
import com.ispirit.digitalsky.domain.FlightInformationRegion;
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
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Validator;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
public class ApplicationConfiguration {

    public static final String OBJECT_MAPPER = "object.mapper";
    public static final String FLY_DRONE_SERVICE = "fly.drone.service";
    public static final String DRONE_TYPE_SERVICE = "drone.type.service";

    @Value("${DEFAULT_FROM_EMAIL_ID:no-reply@dgca.gov.in}")
    private String defaultFromEmailId;

    @Value("${SEND_GRID_API_KEY:default}")
    private String sendGridApiKey;

    @Value("${RESET_PASSWORD_PATH:http://localhost:3000/resetPassword}")
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

    @Value("${MANUFACTURER_DIGITAL_CERT_MANUFACTURER_ATTRIBUTE_NAME:o}")
    private String manufacturerDigitalCertManufacturerAttributeName;

    @Value("${MANUFACTURER_DIGITAL_CERT_VALIDATION_ENABLED:true}")
    private boolean manufacturerDigitalCertValidationEnabled;

    @Value("${self-signed-validity:false}")
    private String selfSignedValidity;

    @Value("${CCA_CERT_PATH:classpath:CCAcertificate.pem}")
    private String ccaCertificatePath;

    private List<FlightInformationRegion> firs = new ArrayList<>();

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
    AdcNumberServiceImpl adcNumberService(AdcNumberRepository adcNumberRepository){
        return new AdcNumberServiceImpl(adcNumberRepository);
    }

    @Bean
    FicNumberServiceImpl ficNumberService(FicNumberRepository ficNumberRepository){
        return new FicNumberServiceImpl(ficNumberRepository);
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

    @Bean(DRONE_TYPE_SERVICE)
    DroneTypeService droneTypeService(DroneTypeRepository droneTypeRepository, StorageService storageService) {
        return new DroneTypeServiceImpl(droneTypeRepository, storageService);
    }

    @Bean
    @DependsOn(DRONE_TYPE_SERVICE)
    DroneDeviceService droneDeviceService(DroneDeviceRepository droneRepository, DigitalSignatureVerifierService signatureVerifierService,
                                          IndividualOperatorRepository individualOperatorRepository,
                                          OrganizationOperatorRepository organizationOperatorRepository,
                                          OperatorDroneService operatorDroneService,
                                          ManufacturerService manufacturerService,
                                          DroneTypeServiceImpl droneTypeService) {
        return new DroneDeviceServiceImpl(droneRepository, signatureVerifierService,individualOperatorRepository, organizationOperatorRepository, operatorDroneService, manufacturerService, droneTypeService );
    }

    @Bean
    @DependsOn(OBJECT_MAPPER)
    DigitalSignatureVerifierService signatureVerifierService(ManufacturerService manufacturerService, DigitalCertificateValidatorService digitalCertificateValidatorService) {
        return new DigitalSignatureVerifierServiceImpl(digitalCertificateValidatorService, manufacturerDigitalCertManufacturerAttributeName, manufacturerDigitalCertValidationEnabled,myObjectMapper());
    }

    @Bean
    DigitalCertificateValidatorService digitalCertificateValidatorService() {
        return new DigitalCertificateValidatorServiceImpl(Boolean.valueOf(selfSignedValidity),ccaCertificatePath);
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

    @Bean(FLY_DRONE_SERVICE)
    FlyDronePermissionApplicationService flyDronePermissionApplicationService(FlyDronePermissionApplicationRepository repository, StorageService storageService, AirspaceCategoryService airspaceCategoryService, freemarker.template.Configuration freemarkerConfiguration, DigitalSignService digitalSignService, OperatorDroneService operatorDroneService, UserProfileService userProfileService, PilotService pilotService, AdcNumberRepository adcNumberRepository, FicNumberRepository ficNumberRepository){
        return new FlyDronePermissionApplicationServiceImpl(repository, storageService, airspaceCategoryService, digitalSignService, operatorDroneService, userProfileService, pilotService, freemarkerConfiguration, this.firs, adcNumberService(adcNumberRepository), ficNumberService(ficNumberRepository) );
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

    @Bean(OBJECT_MAPPER)
    public ObjectMapper myObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return objectMapper;
    }

    @Bean
    @DependsOn(OBJECT_MAPPER)
    public List<FlightInformationRegion> getFirs(){
        try {
            File file = new File("chennaiFir.json");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            firs.add(0, new FlightInformationRegion("Chennai", reader.readLine(), 'O'));
            file = new File("delhiFir.json");
            reader = new BufferedReader(new FileReader(file));
            firs.add(1, new FlightInformationRegion("Delhi",reader.readLine() , 'I'));
            file = new File("mumbaiFir.json");
            reader = new BufferedReader(new FileReader(file));
            firs.add(2, new FlightInformationRegion("Mumbai", reader.readLine(), 'A'));
//            file = new File("kolkataFir.json");
//            reader = new BufferedReader(new FileReader(file));
//            firs.add(3, new FlightInformationRegion("Kolkata", reader.readLine(), 'E')); todo: replace with actual kolkata geojson when we get it properly
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return firs;
    }

}
