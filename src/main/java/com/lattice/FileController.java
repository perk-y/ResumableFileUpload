package com.lattice;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class FileController {

    @PostMapping(value = "/fileUpload")
    public ResponseEntity<?> uploadFileInPartsStream(HttpServletResponse response,
                                                     HttpServletRequest request
    ) throws IOException, ServletException, FileUploadException {

        JSONObject res = new JSONObject();
        String filename = request.getHeader("filename");
        System.out.println("Filename : " + filename);
        long fileSize = Long.parseLong(request.getHeader("fileSize"));
        File f = new File("C:\\Users\\Sakshi\\Documents\\" + filename);
        if (f.exists() && !f.isDirectory()) {
            if (fileSize == f.length()) {
                res.put("message", "File already exists on server");
            } else {
                System.out.println("Started  Partial Upload");

                BytesWritten bytesWritten = new BytesWritten();
                ServletFileUpload upload = new ServletFileUpload();
                ProgressListener progressListener = new ProgressListener() {
                    private long megaBytes = -1;

                    public void update(long pBytesRead, long pContentLength, int pItems) {
                        long mBytes = pBytesRead / 1000000;
                        if (megaBytes == mBytes) {
                            return;
                        }
                        megaBytes = mBytes;
//                System.out.println("We are currently reading item " + pItems);
                        if (pContentLength == -1) {
                            System.out.println("So far, " + pBytesRead + " bytes have been read.");
                            if (response.getHeader("Content-Range") != null) {
                                response.setHeader("Content-Range", Long.toString(pBytesRead));
                                bytesWritten.setBytesWritten(pBytesRead);
                            } else {
                                response.addHeader("Content-Range", Long.toString(pBytesRead));

                                bytesWritten.setBytesWritten(pBytesRead);

                            }
                        } else {
                            System.out.println("So far, " + pBytesRead + " of " + pContentLength
                                    + " bytes have been read.");
                            if (response.getHeader("Content-Range") != null) {
                                response.setHeader("Content-Range", Long.toString(pBytesRead));
                                bytesWritten.setBytesWritten(pBytesRead);

                            } else {
                                response.addHeader("Content-Range", Long.toString(pBytesRead));
                                bytesWritten.setBytesWritten(pBytesRead);

                            }

                        }
                    }
                };
                upload.setProgressListener(progressListener);
                FileItemIterator iterStream = upload.getItemIterator(request);


                while (iterStream.hasNext()) {
                    FileItemStream item = iterStream.next();

                    InputStream stream = item.openStream();
                    if (!item.isFormField()) {
                        // Process the InputStream
                        try (OutputStream out = new FileOutputStream("C:\\Users\\Sakshi\\Documents\\" + item.getName(), true)) {
                            IOUtils.copy(stream, out);
                            System.out.println("No of bytes Written: " + bytesWritten.getBytesWritten());
                        }
                    } else {
                        System.out.println("No file associated");
                    }
                }
            }
            return ResponseEntity.ok(res.toString());
        } else {
            System.out.println("Started Upload");


            BytesWritten bytesWritten = new BytesWritten();
            ServletFileUpload upload = new ServletFileUpload();
            ProgressListener progressListener = new ProgressListener() {
                private long megaBytes = -1;

                public void update(long pBytesRead, long pContentLength, int pItems) {
                    long mBytes = pBytesRead / 1000000;
                    if (megaBytes == mBytes) {
                        return;
                    }
                    megaBytes = mBytes;
//                System.out.println("We are currently reading item " + pItems);
                    if (pContentLength == -1) {
                        System.out.println("So far, " + pBytesRead + " bytes have been read.");
                        if (response.getHeader("Content-Range") != null) {
                            response.setHeader("Content-Range", Long.toString(pBytesRead));
                            bytesWritten.setBytesWritten(pBytesRead);
                        } else {
                            response.addHeader("Content-Range", Long.toString(pBytesRead));
                            bytesWritten.setBytesWritten(pBytesRead);

                        }
                    } else {
                        System.out.println("So far, " + pBytesRead + " of " + pContentLength
                                + " bytes have been read.");
                        if (response.getHeader("Content-Range") != null) {
                            response.setHeader("Content-Range", Long.toString(pBytesRead));
                            bytesWritten.setBytesWritten(pBytesRead);

                        } else {
                            response.addHeader("Content-Range", Long.toString(pBytesRead));
                            bytesWritten.setBytesWritten(pBytesRead);

                        }

                    }
                }
            };
            upload.setProgressListener(progressListener);
            FileItemIterator iterStream = upload.getItemIterator(request);


            while (iterStream.hasNext()) {
                FileItemStream item = iterStream.next();

                InputStream stream = item.openStream();
                if (!item.isFormField()) {
                    // Process the InputStream
                    try (OutputStream out = new FileOutputStream("C:\\Users\\Sakshi\\Documents\\" + item.getName())) {
                        IOUtils.copy(stream, out);
                        System.out.println("No of bytes Written: " + bytesWritten.getBytesWritten());
                    }
                } else {
                    System.out.println("No file associated");
                }
            }

        }
        return ResponseEntity.ok("Successfully uploaded file");

    }

    @GetMapping("/fileExists")
    public ResponseEntity<?> checkIfFileExists(@RequestParam String filename, @RequestParam Integer fileSize) {
        System.out.println(filename);
        File f = new File("C:\\Users\\Sakshi\\Documents\\" + filename);

        JSONObject res = new JSONObject();

        if (f.exists() && !f.isDirectory()) {
            if (fileSize == f.length()) {
                res.put("message", "Complete file exists ");
                return ResponseEntity.ok(res.toString());
            } else {
                res.put("message", "Partial file exists");
                res.put("bytes", f.length());
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(res.toString());
            }
        } else {
            res.put("message", "File does not exists");
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(res.toString());

        }

    }
}
