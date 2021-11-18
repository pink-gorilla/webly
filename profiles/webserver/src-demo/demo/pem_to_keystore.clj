(ns demo.pem-to-keystore)

;; Concept:
;;
;; Converts a .pem and a .crt to a keystore.jks (p12) file. The first
;; two files are the result of a letsencrypt process, while the latter
;; format is required for Java web servers like Jetty.
;;
;; Based on this example: https://stackoverflow.com/a/26678732

(when (nil? (java.security.Security/getProvider "BC"))
  (java.security.Security/addProvider
   (org.bouncycastle.jce.provider.BouncyCastleProvider.)))

(defn- read-private-key
  [pem-file]
  (with-open [reader (java.io.FileReader. pem-file)
              pem (org.bouncycastle.openssl.PEMParser. reader)]
    (let [pem-key-pair (.readObject pem)
          jca-pem-key-converter (org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter.)
          _ (.setProvider jca-pem-key-converter
                          "BC")
          key-pair (.getKeyPair jca-pem-key-converter
                                pem-key-pair)
          private-key (.getPrivate key-pair)]
      private-key)))

(defn- read-certificate
  [certificat-file]
  (with-open [reader (java.io.FileReader. certificat-file)
              pem (org.bouncycastle.openssl.PEMParser. reader)]
    (let [cert-holder (.readObject pem)
          certificate (-> (org.bouncycastle.cert.jcajce.JcaX509CertificateConverter.)
                          (.setProvider "BC")
                          (.getCertificate cert-holder))]
      certificate)))

(defn convert-pem-to-p12
  [{:keys [pem-file password certificat-file]}]
  (with-open [bos (java.io.ByteArrayOutputStream.)]
    (let [key-store (java.security.KeyStore/getInstance "PKCS12")]
      (.load key-store
             nil)
      (.setKeyEntry key-store
                    "alias"
                    (read-private-key pem-file)
                    (.toCharArray password)
                    (into-array java.security.cert.Certificate
                                [(read-certificate certificat-file)]))
      (.store key-store
              bos
              (.toCharArray password))
      (.toByteArray bos))))

(comment
  (with-open [out (java.io.FileOutputStream. "example-results/keystore.jks")]
    (.write out
            (convert-pem-to-p12
             {:pem-file (java.io.File. "example-results/domain.key")
              :password "secret"
              :certificat-file (java.io.File. "example-results/domain-chain.crt")}))))
