/*  References ds_organization,  id = ds_organization.id */
CREATE TABLE ds_manufacturer (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    RESOURCE_OWNER_ID INT,
    STATUS VARCHAR(10)
);