package com.golubovicluka.passwordmanagementsystem.model;

/**
 * Represents a password entry in the password management system.
 * This class corresponds to the 'password_entries' table in the database and
 * stores
 * website credentials for users.
 */
public class PasswordEntry {
    private String username;
    private String password;
    private String website;
    private int id;
    private String websiteUrl;

    /**
     * Constructor for creating a new password entry with basic information
     *
     * @param username the username for the website login
     * @param password the password for the website login
     * @param website  the name of the website
     */
    public PasswordEntry(String username, String password, String website) {
        this.username = username;
        this.password = password;
        this.website = website;
    }

    /**
     * Gets the username associated with this password entry
     *
     * @return the username for the website login
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username for this password entry
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password associated with this entry
     *
     * @return the password for the website login
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password for this entry
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the website name associated with this entry
     *
     * @return the website name
     */
    public String getWebsite() {
        return website;
    }

    /**
     * Sets the website name for this entry
     *
     * @param website the website name to set
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * Sets the unique identifier for this password entry
     *
     * @param id the unique identifier to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the unique identifier of this password entry
     *
     * @return the entry's unique identifier
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the website URL for this entry
     *
     * @param websiteUrl the full URL of the website
     */
    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    /**
     * Gets the website URL associated with this entry
     *
     * @return the full URL of the website
     */
    public String getWebsiteUrl() {
        return websiteUrl;
    }
}