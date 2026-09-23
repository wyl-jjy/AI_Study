package com.example.springai_01.tools;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.example.springai_01.Entity.Po.Course;
import com.example.springai_01.Entity.Po.CourseReservation;
import com.example.springai_01.Entity.Po.School;
import com.example.springai_01.query.CourseQuery;
import com.example.springai_01.service.ICourseReservationService;
import com.example.springai_01.service.ICourseService;
import com.example.springai_01.service.ISchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CourseTool {

    private final ICourseService iCourseService;
    private final ISchoolService iSchoolService;
    private final ICourseReservationService iCourseReservationService;

    @Tool(description = "根据条件查询课程")
    public List<Course> queryCourse(@ToolParam(required = false, description = "课程查询条件")CourseQuery query){
        QueryChainWrapper<Course> wrapper = iCourseService.query();
        wrapper
                .eq(query.getType() != null, "type", query.getType())
                .le(query.getEdu() != null, "edu", query.getEdu());
        if(query.getSorts() != null) {
            for (CourseQuery.Sort sort : query.getSorts()) {
                wrapper.orderBy(true, sort.getAsc(), sort.getField());
            }
        }
        return wrapper.list();
    }
    @Tool(description = "查询所有校区")
    public List<School> queryAllSchools() {
        return iSchoolService.list();
    }

    @Tool(description = "生成课程预约单,并返回生成的预约单号")
    public String generateCourseReservation(
            @ToolParam(description = "预约课程") String courseName,
            @ToolParam(description = "学生姓名") String studentName,
            @ToolParam(description = "联系电话") String contactInfo,
            @ToolParam(description = "预约校区") String school,
            @ToolParam(description = "备注",required = false) String remark) {
        CourseReservation courseReservation = new CourseReservation();
        courseReservation.setCourse(courseName);
        courseReservation.setStudentName(studentName);
        courseReservation.setContactInfo(contactInfo);
        courseReservation.setSchool(school);
        courseReservation.setRemark(remark);
        iCourseReservationService.save(courseReservation);
        return String.valueOf(courseReservation.getId());
    }
}
