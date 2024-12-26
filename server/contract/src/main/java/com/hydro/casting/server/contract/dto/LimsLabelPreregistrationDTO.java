package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.LocalDateTime;
import java.util.Objects;

public class LimsLabelPreregistrationDTO implements ViewDTO  {

    private Long rwsampleid;
    private LocalDateTime datum;
    private String legnr;
    private String charge;
    private String probe;
    private String ofen;
    private String herkunft;
    private String mangel;

    @Override
    public long getId() {
        return this.hashCode();
    }

    public LimsLabelPreregistrationDTO() {
    }

    public LimsLabelPreregistrationDTO(Long rwsampleid, LocalDateTime datum, String legnr, String charge, String probe, String ofen, String herkunft, String mangel) {
        this.rwsampleid = rwsampleid;
        this.datum = datum;
        this.legnr = legnr;
        this.charge = charge;
        this.probe = probe;
        this.ofen = ofen;
        this.herkunft = herkunft;
        this.mangel = mangel;
    }

    public Long getRwsampleid() {
        return rwsampleid;
    }

    public void setRwsampleid(Long rwsampleid) {
        this.rwsampleid = rwsampleid;
    }

    public LocalDateTime getDatum() {
        return datum;
    }

    public void setDatum(LocalDateTime datum) {
        this.datum = datum;
    }

    public String getLegnr() {
        return legnr;
    }

    public void setLegnr(String legnr) {
        this.legnr = legnr;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getProbe() {
        return probe;
    }

    public void setProbe(String probe) {
        this.probe = probe;
    }

    public String getOfen() {
        return ofen;
    }

    public void setOfen(String ofen) {
        this.ofen = ofen;
    }

    public String getHerkunft() {
        return herkunft;
    }

    public void setHerkunft(String herkunft) {
        this.herkunft = herkunft;
    }

    public String getMangel() {
        return mangel;
    }

    public void setMangel(String mangel) {
        this.mangel = mangel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LimsLabelPreregistrationDTO that = (LimsLabelPreregistrationDTO) o;
        return Objects.equals(rwsampleid, that.rwsampleid) && Objects.equals(datum, that.datum) && Objects.equals(legnr, that.legnr) && Objects.equals(charge, that.charge) && Objects.equals(probe, that.probe) && Objects.equals(ofen, that.ofen) && Objects.equals(herkunft, that.herkunft) && Objects.equals(mangel, that.mangel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rwsampleid, datum, legnr, charge, probe, ofen, herkunft, mangel);
    }

    @Override
    public String toString() {
        return "LimsLabelPreregistrationDTO{" +
                "rwsampleid=" + rwsampleid +
                ", datum=" + datum +
                ", legnr='" + legnr + '\'' +
                ", charge='" + charge + '\'' +
                ", probe='" + probe + '\'' +
                ", ofen='" + ofen + '\'' +
                ", herkunft='" + herkunft + '\'' +
                ", mangel='" + mangel + '\'' +
                '}';
    }
}
