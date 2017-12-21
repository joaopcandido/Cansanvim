/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUIs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author joaopcandido
 */
public class VendasBD {

    public String getCmd() {
        return cmd;
    }
    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getPws() {
        return pws;
    }
    public void setPws(String pws) {
        this.pws = pws;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    
    public ResultSet getReturnquery() {
        return returnquery;
    }

    public void setReturnquery(ResultSet returnquery) {
        this.returnquery = returnquery;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public Statement getStm() {
        return stm;
    }

    public void setStm(Statement stm) {
        this.stm = stm;
    }

    public ResultSet getReturndata() {
        return returndata;
    }

    public void setReturndata(ResultSet returndata) {
        this.returndata = returndata;
    }
    
    private String cmd;
    private String pws = "jpcn2503";
    private String user = "postgres";
    private String url = "jdbc:postgresql://localhost:5433/ProjetoFinal";
    private ResultSet returnquery;
    private ResultSet returndata;
    private Connection conn;
    private Statement stm;
    
    public VendasBD(){
        
    }
    
    //Métodos:
    
    public void conectar(){
        try {
            Class.forName("org.postgresql.Driver");
            this.conn = DriverManager.getConnection(url, user, pws);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    
    public void desconectar(){
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(VendasBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void inserirDados(String produto, String idAnimal, float quant, float preco, String comprador, String data){
        float total = quant*preco;
        float saldo = 0;
        cmd = "INSERT INTO cansanvim(produto, idanimal, quantidade, preco, total, comprador, data) VALUES('"
                + produto + "','" + idAnimal + "'," + quant + "," + preco + "," + total + ",'" + comprador + "','" + data + "')";
        try {
            //Criando uma instancia de comando a ser executado
            stm = conn.createStatement();
            //Solicitando a execução do comando
            stm.executeUpdate(cmd);
            cmd = "SELECT * from financas ORDER BY transacao";
            returnquery = stm.executeQuery(cmd);
            while(returnquery.next())
                if(returnquery.getString("tipotransacao").equals("( + )"))
                    saldo = saldo + Float.parseFloat(returnquery.getString("valor"));
                else
                    saldo = saldo - Float.parseFloat(returnquery.getString("valor"));
            cmd = "INSERT INTO financas(datatransacao, valor, tipotransacao, saldoatual) VALUES('" + data + "'," + total 
                    + ",'( + )'," + (saldo + total) + ")";
            stm.executeUpdate(cmd);
            if (produto.equals("Corte")){
                cmd = "UPDATE gadosteste2 SET status ='Vendido' where codIdentificacao ='"+idAnimal+"'";
                try {
                    stm.executeUpdate(cmd);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, ex);
                }
            }
            JOptionPane.showMessageDialog(null, "Registro inserido com sucesso!!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }

    }
    
    public void verRegistro(){
        cmd = "SELECT * from cansanvim ORDER BY id";
        try {
            //Criar uma instância para a execução do comando
            stm = conn.createStatement();
            //Execução do comando de consulta no banco e armazenando na lista
            setReturnquery(stm.executeQuery(cmd));           
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    
    public void registroPorData(String data1, String data2){
        if (!data1.equals("Ano-Mes-Dia") && data2.equals("Ano-Mes-Dia")){
            cmd = "SELECT * from cansanvim where data >= '" + data1 + "' ORDER BY id";
            try {
                //Criar uma instância para a execução do comando
                stm = conn.createStatement();
                //Execução do comando de consulta no banco e armazenando na lista
                setReturndata(null);
                setReturndata(stm.executeQuery(cmd));           
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
        else if (data1.equals("Ano-Mes-Dia") && !data2.equals("Ano-Mes-Dia")){
            cmd = "SELECT * from cansanvim where data <= '" + data2 + "' ORDER BY id";
            try {
                //Criar uma instância para a execução do comando
                stm = conn.createStatement();
                //Execução do comando de consulta no banco e armazenando na lista
                setReturndata(null);
                setReturndata(stm.executeQuery(cmd));           
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
        else{
            cmd = "SELECT * from cansanvim where data between '" + data1 + "' and '" + data2 + "' ORDER BY id";
            try {
                //Criar uma instância para a execução do comando
                stm = conn.createStatement();
                //Execução do comando de consulta no banco e armazenando na lista
                setReturndata(null);
                setReturndata(stm.executeQuery(cmd));           
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
    }
}
