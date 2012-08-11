<?php

/**
 * PHP based Gateway.
 * 
 * @author zeising.tobias <br>
 *         copyright (C) 2012, http://www.aditu.de, tobias.zeising@aditu.de
 */

// configuration
$proxyName = '';
$proxyPort = 8080;
$proxySSLPort = 8080;
$proxyUser = '';
$proxyPass = '';
$transporter = 'tcp://';

// default public key if no file "publickey" in current directory was found
$defaultPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAn+EYTIpP6TJM1njwZiToalYP8idtB35IoivR+3ovVXUJyxjUxQiBl5TAkHWO/w79ZM3XFJEbggq6O8W9a0mIxEoyHJHCD+/Xn9+nIG1dQKJmY85hKCpvqjB3MqD35z4ku/uYeKNuYr35/Z9HUdO+qX/HGQBL6P+nKhQiiFhGErZ6dt5NYZ1VocGQi4fnDkiv4+LZOBAEREjkwy02x0i/f2wzZVqC96XOdlLnKd3aKEBm9+uiro+1XoU23qw8NXrbJeSB7dkJK+XrQSOlIVSMyf9SncLs5d/oQ2OubUSMRG6RfyrQWk4NkgfJwjP+6IZc4FHru2ODLlEjcGPY0gCUhQIDAQAB";

// default private key if no file "privatekey" in current directory was found
$defaultPrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCf4RhMik/pMkzWePBmJOhqVg/yJ20HfkiiK9H7ei9VdQnLGNTFCIGXlMCQdY7/Dv1kzdcUkRuCCro7xb1rSYjESjIckcIP79ef36cgbV1AomZjzmEoKm+qMHcyoPfnPiS7+5h4o25ivfn9n0dR076pf8cZAEvo/6cqFCKIWEYStnp23k1hnVWhwZCLh+cOSK/j4tk4EARESOTDLTbHSL9/bDNlWoL3pc52Uucp3dooQGb366Kuj7VehTberDw1etsl5IHt2Qkr5etBI6UhVIzJ/1Kdwuzl3+hDY65tRIxEbpF/KtBaTg2SB8nCM/7ohlzgUeu7Y4MuUSNwY9jSAJSFAgMBAAECggEAaU/bMqe0xZXleSzGpXHQSiMQc+C2d8YeT/xvX6f3P+JNJiPgGz1kJ5XLVhypqaaVEO5pq1l99iXc0HyLLf2NAkchRSJZ4yeIL/4hV0HSuD2BFmMeKlfxn/g2u/m98etERTMQFv8IOjCLWB2v292PbJUHb5d7aSnlUdlbedYzqiTQb7p8+gGRRxdHOGBMgH69VQrvtkXXqeJctc6z7+ED6PitvxsVrja2YoUMg0nA5JCzytZ/EjYa6TAlKFTQekmULeoeqs7/QaQI45j7+qTZyMyx0HCzrcJznB4W5bHAbwI00CHOlozNWeNvLDlaIMzlkw1ktOCdbq9AixRDahnZzQKBgQDNBeAhg/X6Wap+28wI8NsPtOrPHJpsAiDNOjJ8lg6dAO99dslMZrruX0sYTBIZOsg3appgyd+XBB6Iy1w4eMu/Zc349s6klP4IOntp9EXfNGPHSeCcojsdF84OEExrK31gOI0pu10h3nXLMeOrxSZwzHnqBJi49HHKB5WH6LQBKwKBgQDHobvEVXO4hhxPPOSPrwA6DJubcWJnsqJbXQ1WdgL0pd82cLG3vdADli7RDXkV+G/Q7spDZrxdGUB1OdRJ1k1ULE/jZpPTEiaqoFCY8jyj7qDuX21SfD2YLFnr2AK/3hrjflsZHN2+OwvrJnyP06YxTodvS9bkQHmAwpcLOT4JDwKBgDEOQ9ehHHz/cWUTMFm/T5sNfdV6kZRgWfrg/u2CdhKpA6SwhG/McNPSZ0R0ByyUPQ3NawzXoLEgojG2/uTgOlgcgtEcNMt6fuH3JEpw0Tsb7PflPGIeCyjl5j7FNZO6/y/DuDsRArK06NhqooAaFx2MP2t4GBbBHmzpraSsUE2rAoGBAJEseWq2pbfbtzycUK54mhac0pBA7Unx6wW77pYgrdeRxHhHjcXMADiLf7JBNPwYNeBftrE7ReICGhDlpA6qDIQY0WPSVc/Z1Eqi2NnQ1u5z+4490Td56CUAo8fRx3YWI8GqQr9JcawELo+r5kuFo8cZj34NTdGhXXqYh9T+pLFbAoGALRkIRvaQHiy7XnkHo3V6Wb4hU+xnBUiXr+BiTkwP+o748rnzsqtdA+h8QPhPxYVqE/AmqjJj+vdzdAQmDs86tqbKhUB+hLdoEyu+2AAs4nlTqyzVcFaFdh7B6Xt+g58R4LnENUW2RApE32voKwQiLDP8UpuvZ4uplwr29SrxgC4=";

// public keys of clients which are allowed to connect to this gateway
$clientPublicKeys = array(
    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjmSmVTEBu82Trw/ey5U+iwBNPNyxHcstoe9XrVnNrMgxlKL9vx0VQhj6hYzgmL9j02Km7IocuBMAxA32Ln6x+MpV9MXK0Rc+gNaNYDS/AJnx21WQ/isKXU3YvfmI/zb3uN+ebHOkKYvHpie2s0F0jHMGv5evLVy07IAu+8Ab8Xj4fYiUPJOFGeVthEIOtVa8TBLbkw0Ua/oh8da8XRXqOEzq5Y9llwUj5gFjAwwcJOhgHJ0wss9B650+pEoD79xwvjIny0At9E7cNbEWhQkUfflWBeodzThNn/7Tu7gGHrxv5wCq/64MtXDBNGKlgpQ/aGwpAOjoVlIO1zWvD3pi/QIDAQAB"
);

// directory where clients public keys are saved
$dirClientPublicKeys = false;

// optional error page which will be included
$showErrorPage = false;





// to all non php nerds: don't change anything behind this line
///////////////////////////////////////////////////////////////

// include phpseclib
include('Crypt/RSA.php');
include('Crypt/AES.php');

//
// load keys
//

// enable/disable encryption
$useEncryption = true;

// public key
$publicKey = base64_decode($defaultPublicKey);
if(file_exists("privatekey"))
    $publicKey = base64_decode(file_get_contents("privatekey"));

// private key
$privateKey = base64_decode($defaultPrivateKey);
if(file_exists("publickey"))
    $privateKey = base64_decode(file_get_contents("publickey"));

// allowed public keys of clients

// use clients public keys if directory was set
if($dirClientPublicKeys!=false && file_exists($dirClientPublicKeys) && $handle = opendir($dirClientPublicKeys)) {
    $clientPublicKeys = array();
    while (false !== ($file = readdir($handle)))
        if ($file != "." && $file != "..")
            $clientPublicKeys[] = file_get_contents($dirClientPublicKeys . "/" . $file);
    closedir($handle);
} 

// decode clients public keys
for($i=0;$i<count($clientPublicKeys);$i++)
    $clientPublicKeys[$i] = base64_decode($clientPublicKeys[$i]);



//
// functions
//
 
// shows error message or error page
function error($error) {
    if($showErrorPage===false)
        die($error);
    else {
        include $showErrorPage;
        die();
    }
}

// function for parsing the header. Taken from PHP PECL.
function http_parse_headers($header) {
    $retVal = array();
    $fields = explode("\r\n", preg_replace('/\x0D\x0A[\x09\x20]+/', ' ', $header));
    foreach( $fields as $field ) {
        if( preg_match('/([^:]+): (.+)/m', $field, $match) ) {
            $match[1] = preg_replace('/(?<=^|[\x09\x20\x2D])./e', 'strtoupper("\0")', strtolower(trim($match[1])));
            if( isset($retVal[$match[1]]) ) {
                $retVal[$match[1]] = array($retVal[$match[1]], $match[2]);
            } else {
                $retVal[$match[1]] = trim($match[2]);
            }
        }
    }
    return $retVal;
}

//
// read post data
//
$encryptedRequest = file_get_contents('php://input');
if(strlen(trim($encryptedRequest))==0)
    error("no value given");

if($useEncryption===true) {    
    $encryptedRequest = base64_decode($encryptedRequest);


    //
    // read AES password from request by decrypting with RSA
    //
    $rsa = new Crypt_RSA();
    $rsa->setEncryptionMode(CRYPT_RSA_ENCRYPTION_PKCS1);
    $rsa->loadKey($privateKey);
    $aesPassword = $rsa->decrypt(substr($encryptedRequest, 0, 256));

    //
    // decrypt request with AES
    //
    $aes = new Crypt_AES(CRYPT_AES_MODE_ECB);

    // PKCS5 Padding used by java
    $aes->enablePadding();

    // password is the first 16 byte of a SHA 256 hashed given aes password
    $aesPasswordHashed = substr(hash("sha256", $aesPassword, true), 0, 16);

    $aes->setKey($aesPasswordHashed);
    $request = $aes->decrypt(substr($encryptedRequest, 256));

    //
    // check sign of the request
    //
    // TODO

} else {
    $request = $encryptedRequest;
}


//
// prepare proxy request
//

// get target url from header (host)
$host = "";
$port = 80;
if(isset($_GET['ssl']))
    $port = 443;

$headers = http_parse_headers($request);
foreach($headers as $key => $value) {
    // get host and port from headers
    if(strtolower($key)=="host") {
        $hostParts = preg_split("/:/", $value);
        $host = $hostParts[0];
        if(count($hostParts)>1) {
            $port = $hostParts[1];
        }
    }
}
if(strlen(trim($host))==0)
    error("no host found");

// add connection close for preventing endless blocking on fread
$headers["Connection"] = "close";

// proxy set? use it.
if(strlen($proxyName)!=0) {
    $host = $proxyName;
    
    if(isset($_GET['ssl']))
        $port = $proxySSLPort;
    else
        $port = $proxyPort;
    
    // use proxy authentication
    if(strlen(trim($proxyUser))!=0)
        $headers["Proxy-Authorization"] = "Basic " . base64_encode ("$proxyUser:$proxyPass");
}

// generate new header
$header = substr($request, 0, strpos($request, "\r\n")) . "\r\n";
foreach($headers as $key => $value) {
    $header = $header . $key . ": " . $value . "\r\n";
}

// get request body
$requestParts = preg_split("/\r\n\r\n/", $request);
$body = "";
if(count($requestParts)>1)
    $body = $requestParts[1];
    
// https connection?
if(isset($_GET['ssl']))
    $transporter = "tls://";

//
// send http request
//

$host = $transporter . $host;
$timeout = 20;
$connection = fsockopen($host, $port, $errno, $errstr, $timeout);
if(!$connection)
    error("Connection Error");

$request = $header."\r\n".$body."\r\n\r\n";
fputs($connection, $request);

$response = '';
while(!feof($connection)) {
    $response .= fread($connection, 128);
}
fclose($connection);

//
// sign
//

//
// encrypt
//

//
// send response
//
header("Content-Length: " . strlen($response));
die($response);