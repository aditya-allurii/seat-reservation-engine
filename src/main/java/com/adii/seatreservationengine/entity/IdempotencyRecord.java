package com.adii.seatreservationengine.entity;


import jakarta.persistence.*;

@Entity
public class IdempotencyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(name = "Idempotency_Key",unique = true,nullable = false)
    private String key;

    private Integer statusCode;

    @Column
    private String response;

    public Long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    private Long seatId;

    public  Long getSeatId() {
        return seatId;
    }

    public  void setSeatId(Long seatId) {
        this.seatId = seatId;
    }


}
