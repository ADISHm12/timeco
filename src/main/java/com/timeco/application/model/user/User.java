package com.timeco.application.model.user;

import com.timeco.application.model.cart.Cart;
import com.timeco.application.model.coupon.Coupon;
import com.timeco.application.model.role.Role;
import com.timeco.application.model.wallet.Wallet;
import com.timeco.application.model.wishlist.Wishlist;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;


@Entity
@Table(name =  "user", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User  {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name",nullable = false)
    private String firstName;

    @Column(name = "last_name",nullable = false)
    private String lastName;

    private String email;

    private String phoneNumber;

    private String password;

    private boolean isBlocked;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))

    private Collection<Role> roles;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserAddress> addresses = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "customer_coupon",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "coupon_id"))
    private Set <Coupon> coupons = new HashSet<>();

    @OneToOne(mappedBy = "user",cascade=CascadeType.ALL)
    private Wishlist  wishlist = new Wishlist();


    @ManyToOne
    @JoinColumn(name = "referrer_id")
    private User referrer;  // Referrer

    @Column(name = "referral_code", unique = true)
    private String referralCode;


    @OneToOne(mappedBy = "user",cascade =CascadeType.ALL,fetch = FetchType.LAZY)
    private Wallet  wallet;


    public User getReferrer() {
        return referrer;
    }

    public void setReferrer(User referrer) {
        this.referrer = referrer;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public Wishlist getWishlist() {
        return wishlist;
    }

    public void setWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
    }

    public Set<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(Set<Coupon> coupons) {
        this.coupons = coupons;
    }

    public User() {

    }

    public User(String firstName, String lastName, String email, String phoneNumber, String password, Collection<Role> roles) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber=phoneNumber;
        this.password = password;
        this.roles = roles;


    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallets(Wallet wallets) {
        this.wallet = wallets;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }



    public String getPassword() {
        return password;
    }

    public List<UserAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<UserAddress> addresses) {
        this.addresses = addresses;
    }

    public User(Long id, String firstName, String lastName, String email, String phoneNumber, String password, boolean isBlocked, Collection<Role> roles, List<UserAddress> addresses) {
        super();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.isBlocked = isBlocked;
        this.roles = roles;
        this.addresses = addresses;
        this.cart = new Cart();
        this.cart.setUser(this);
    }

    //===================================================
    public void setPassword(String password) {
        this.password = password;
    }
    public Collection<Role> getRoles() {
        return roles;
    }
    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public User(Long id, String firstName, String lastName, String email, String phoneNumber, String password, boolean isBlocked, Collection<Role> roles) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.isBlocked = isBlocked;
        this.roles = roles;
        this.cart = new Cart();
        this.cart.setUser(this);
    }

    public User(String firstName, String lastName, String email, String phoneNumber, String password, boolean isBlocked, Collection<Role> roles, Cart cart, List<UserAddress> addresses, Set<Coupon> coupons, Wishlist wishlist, User referrer, String referralCode, Wallet wallet) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.isBlocked = isBlocked;
        this.roles = roles;
        this.cart = cart;
        this.addresses = addresses;
        this.coupons = coupons;
        this.wishlist = wishlist;
        this.referrer = referrer;
        this.referralCode = referralCode;
        this.wallet = wallet;
    }
}