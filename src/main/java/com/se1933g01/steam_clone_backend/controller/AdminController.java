// package com.se1933g01.steam_clone_backend.controller;

// import java.util.HashMap;
// import java.util.Map;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatusCode;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.RestController;

// import com.se1933g01.steam_clone_backend.service.RequestService;

// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;


// @RestController
// @RequestMapping("/admin")
// public class AdminController {
//     @Autowired
//     RequestService requestService;
//     @PostMapping("/approve/{requestID}")
//     public ResponseEntity<Map<String,String>> approveGame(@PathVariable String requestID){
//         try {
//             requestService.approveGame(Long.parseLong(requestID), 2L);
//             Map<String,String> response = new HashMap<>();
//             response.put("message", "Game Approved");
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             Map<String,String> response = new HashMap<>();
//             response.put("message", "Game Approve Failed");
//             response.put("e", e.getMessage());
//             return ResponseEntity.badRequest().body(response);
//         }
//     }

    
// }
