package csulb.cecs323.model;

import javax.persistence.*;

/**
 * Individual, physical automobiles that someone can drive on land to transport one or more passengers
 * and a limited amount of cargo around.  Cars have four wheels and usually travel on paved roads.
 */

@Entity
public class Cars {
    /** The unique ID of the vehicle.  Limited to 17 characters. */
    @Id
    @Column (length=17, nullable = false)
    private String VIN;

    /** The name of the corporation which manufactured the vehicle.  Limited to 40 characters. */
    @Column (length=40, nullable = false)
    private String manufacturer;

    /** The popular name of the vehicle, like the Prius for Toyota.  Limited to 20 characters. */
    @Column (length=20, nullable = false)
    private String model;

    /** The year that the vehicle was manufactured.  For now, do not worry about validating this #. */
    @Column (nullable = false)
    private int model_year;

    @Override
    public String toString () {
        return "Cars - VIN: " + this.VIN + " Manufacturer: " + this.manufacturer +
                " Model: " + this.model + " model_year: " + this.model_year;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owners owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auto_body_style_name", referencedColumnName = "name", nullable = false)
    private auto_body_styles auto_body_style;

    /** Default constructor */
    public Cars() {}

    /** Constructor */
    public Cars(Owners owner, auto_body_styles auto_body_style, String VIN, String manufacturer, String model, int model_year) {
        this.setOwner(owner);
        this.setAuto_body_style(auto_body_style);
        this.setVIN(VIN);
        this.setManufacturer(manufacturer);
        this.setModel(model);
        this.setModel_year(model_year);
    }

    /** Getter methods */
    public csulb.cecs323.model.Owners getOwner() {
        return owner;
    }

    public csulb.cecs323.model.auto_body_styles getAuto_body_style() {
        return auto_body_style;
    }

    public String getVIN() {
        return VIN;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public int getModel_year() {
        return model_year;
    }

    /** Setter methods */
    public void setOwner(csulb.cecs323.model.Owners owner) {
        this.owner = owner;
    }

    public void setAuto_body_style(csulb.cecs323.model.auto_body_styles auto_body_style) {
        this.auto_body_style = auto_body_style;
    }

    public void setVIN(String VIN) {
        this.VIN = VIN;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setModel_year(int model_year) {
        this.model_year = model_year;
    }

}
