<?php

/**
 * PHP based Gateway.
 * 
 * @author zeising.tobias <br>
 *         copyright (C) 2012, http://www.aditu.de, tobias.zeising@aditu.de
 */

// optional proxy config
$proxyName = '';
$proxyPort = 8080;
$proxySSLPort = 8080;
$proxyUser = '';
$proxyPass = '';
$transporter = 'tcp://';

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

// read post data
$request = file_get_contents('php://input');
if(strlen(trim($request))==0)
    die("no value given");

// decrypt request

// check sign of the request

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
    die("no host found");

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

// send http request
$host = $transporter . $host;
$timeout = 20;
$connection = fsockopen($host, $port, $errno, $errstr, $timeout);
if(!$connection)
    die("Connection Error");

$request = $header."\r\n".$body."\r\n\r\n";
fputs($connection, $request);

$response = '';
while(!feof($connection)) {
    $response .= fread($connection, 128);
}
fclose($connection);

// sign

// encrypt

// send response
header("Content-Length: " . strlen($response));
die($response);