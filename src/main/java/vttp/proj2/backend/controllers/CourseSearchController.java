package vttp.proj2.backend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vttp.proj2.backend.models.CourseSearch;
import vttp.proj2.backend.repositories.UserRepository;
import vttp.proj2.backend.services.CourseHomepageService;
import vttp.proj2.backend.services.CourseSearchService;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class CourseSearchController {
    
    @Autowired
    private CourseSearchService courseSearchSvc;

    @Autowired
    private CourseHomepageService courseHomepageSvc;
    @Autowired
    private UserRepository userRepository;

    //for search
    @GetMapping(path="/courses/search")
    public ResponseEntity<?> search(@RequestParam String query, @RequestParam(required=false) String page, @RequestParam(required=false) String platform, 
                    @RequestParam(required=false) boolean byRating){
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("query", query);
        if (page != null) {
            paramMap.put("page", page);
        }
        if (platform!=null){
            paramMap.put("platform", platform);
        }
        if (byRating){
            paramMap.put("byRating", "byRating");
        }
        List<CourseSearch> foundCourses = courseSearchSvc.courseSearch(paramMap);
        if (null==foundCourses){
            return ResponseEntity.badRequest().body("Error - Bad Request");
        }
        return ResponseEntity.ok(foundCourses);
    }

    @GetMapping(path="/course")
    public ResponseEntity<?> getCourseById(@RequestParam String courseId, @RequestParam String platform){
        System.out.println("course id" + courseId);
        Object courseFound = null;
        switch (platform.toUpperCase()) {
            case "UDEMY":
                courseFound = courseSearchSvc.getUdemyCourseById(courseId);
                break;
            case "EDX":
                break;
            case "COURSERA":
                courseFound = courseSearchSvc.getCourseraCourseById(courseId);
                break;
        }
        if (courseFound != null) {
            return ResponseEntity.ok(courseFound);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //for home page
    @GetMapping(path="/courses/loadhomepage")
    public ResponseEntity<?> getHomepageCourses(){
        List<CourseSearch> homepageCourses = courseHomepageSvc.loadCoursesForHomepage();
        return ResponseEntity.ok(homepageCourses);
    }

    @PostMapping("/courses/unenrollCourse")
    public ResponseEntity<String> unenrollCourse(@RequestParam String courseId) {
        try {
            courseHomepageSvc.unEnrollCourse(courseId); 
            return ResponseEntity.ok("Course unenrolled successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while trying to unenroll the course.");
        }
    }
}
