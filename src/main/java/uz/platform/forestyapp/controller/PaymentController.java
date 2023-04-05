package uz.platform.forestyapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.security.CurrentUser;
import uz.platform.forestyapp.service.EducationCenterService;
import uz.platform.forestyapp.service.PaymentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @Autowired
    EducationCenterService educationCenterService;

    @GetMapping
    public HttpEntity<?> getPayments(@CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = paymentService.getPayments(user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @GetMapping("/finance")
    public HttpEntity<?> getFinanceData(@CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = paymentService.getFinanceData(user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @GetMapping("/employees")
    public HttpEntity<?> getEmployees(@CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = paymentService.getEmployees(user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @GetMapping("/employee/{id}")
    public HttpEntity<?> getEmployee(@CurrentUser User user,@PathVariable("id") UUID id){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = paymentService.getEmployee(user,id);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @GetMapping("/groups")
    public HttpEntity<?> getGroups(@CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = paymentService.getGroups(user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }



    @GetMapping("/group/{id}")
    public HttpEntity<?> getGroup(@CurrentUser User user,@PathVariable("id")UUID id){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = paymentService.getGroup(user,id);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @GetMapping("/teachers")
    public HttpEntity<?> getTeachers(@CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = paymentService.getTeachers(user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @GetMapping("/teacher/{id}")
    public HttpEntity<?> getTeacher(@CurrentUser User user,@PathVariable("id")UUID id){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = paymentService.getTeacher(user,id);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @GetMapping("/teacherGroup/{id}")
    public HttpEntity<?> getTeacherGroupPayments(@CurrentUser User user,@PathVariable("id")UUID id){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = paymentService.getTeacherGroupPayments(user,id);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @GetMapping("/student/{id}")
    public HttpEntity<?> getStudentPayment(@CurrentUser User user,@PathVariable("id")UUID id){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = paymentService.getStudentPayments(user,id);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }


    @PostMapping("/receive")
    public HttpEntity<?> receivePayment(@RequestParam UUID id, @RequestParam Long money, @CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = paymentService.receivePayment(id,money,user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PostMapping("/pay")
    public HttpEntity<?> payToTeacher(@RequestParam UUID id, @RequestParam Long money, @CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = paymentService.payPayment(id,money,user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PostMapping("/salary")
    public HttpEntity<?> payToEmployee(@RequestParam UUID id, @RequestParam Long money, @CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = paymentService.paySalary(id,money,user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }
}
