package com.solinfo.email.model;

public class RegistroServiceRequest {

    private String nitCompania;
    private String nitProveedor;
    private String numFactura;
    private String fechaFactura;
    private String facturaB64;
    private String contrato;
    private String emailProveedor;
    private String asunto;
    private String cufe;
    private String urlApi;
    private int numeroSecuencia;
    private String nombreArchivo;
    private int estadoLectura;
    private String fuenteXml;

    public RegistroServiceRequest() {
    }

    public RegistroServiceRequest(String nitCompania, String nitProveedor, String numFactura, String fechaFactura, String facturaB64, String contrato, String emailProveedor, String asunto, String cufe, String urlApi, int numeroSecuencia, String nombreArchivo, int estadoLectura, String fuenteXml) {
        this.nitCompania = nitCompania;
        this.nitProveedor = nitProveedor;
        this.numFactura = numFactura;
        this.fechaFactura = fechaFactura;
        this.facturaB64 = facturaB64;
        this.contrato = contrato;
        this.emailProveedor = emailProveedor;
        this.asunto = asunto;
        this.cufe = cufe;
        this.urlApi = urlApi;
        this.numeroSecuencia = numeroSecuencia;
        this.nombreArchivo = nombreArchivo;
        this.estadoLectura = estadoLectura;
        this.fuenteXml = fuenteXml;
    }

    public String getNitCompania() {
        return nitCompania;
    }

    public void setNitCompania(String nitCompania) {
        this.nitCompania = nitCompania;
    }

    public String getNitProveedor() {
        return nitProveedor;
    }

    public void setNitProveedor(String nitProveedor) {
        this.nitProveedor = nitProveedor;
    }

    public String getNumFactura() {
        return numFactura;
    }

    public void setNumFactura(String numFactura) {
        this.numFactura = numFactura;
    }

    public String getFechaFactura() {
        return fechaFactura;
    }

    public void setFechaFactura(String fechaFactura) {
        this.fechaFactura = fechaFactura;
    }

    public String getFacturaB64() {
        return facturaB64;
    }

    public void setFacturaB64(String facturaB64) {
        this.facturaB64 = facturaB64;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public String getEmailProveedor() {
        return emailProveedor;
    }

    public void setEmailProveedor(String emailProveedor) {
        this.emailProveedor = emailProveedor;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getCufe() {
        return cufe;
    }

    public void setCufe(String cufe) {
        this.cufe = cufe;
    }

    public String getUrlApi() {
        return urlApi;
    }

    public void setUrlApi(String urlApi) {
        this.urlApi = urlApi;
    }

    public int getNumeroSecuencia() {
        return numeroSecuencia;
    }

    public void setNumeroSecuencia(int numeroSecuencia) {
        this.numeroSecuencia = numeroSecuencia;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public int getEstadoLectura() {
        return estadoLectura;
    }

    public void setEstadoLectura(int estadoLectura) {
        this.estadoLectura = estadoLectura;
    }

    public String getFuenteXml() {
        return fuenteXml;
    }

    public void setFuenteXml(String fuenteXml) {
        this.fuenteXml = fuenteXml;
    }
}
