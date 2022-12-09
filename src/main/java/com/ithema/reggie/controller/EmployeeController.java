package com.ithema.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ithema.reggie.common.R;
import com.ithema.reggie.entity.Employee;
import com.ithema.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("用户名:{} 密码:{}", employee.getUsername(), employee.getPassword());
        //request是用来存储session的
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //根据用户提供的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        //在数据库的设计中，用户名已经设置唯一约束所以可以用getOne
        Employee emp = employeeService.getOne(queryWrapper);
        if (emp == null) {
            return R.error("登录失败");
        }
        //进行密码的比对
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }
        //查看员工状态
        if (emp.getStatus() == 0) {
            return R.error("账号已经禁用");
        }
        //将用户的id放入session中
        request.getSession().setAttribute("employee", emp.getId());
        //将从数据库中查询到的对象返回到前端界面
        return R.success(emp);
    }

    /**
     * 员工退出
     *
     * @param request
     * @return
     */
    //用户退出功能，在写代码的过程中一定要善于使用ctrl c+v
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清除session中当前登录的员工的信息
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }

    /**
     * 新增员工：Request URL: http://localhost/employee
     */
    @PostMapping()
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息:{}", employee.toString());
        //新增员工的时候统一给一个初始密码，员工在登录之后可以自己修改对应的密码
        String password = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(password);
        //设置创建时间和更新数据的时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //获取当前用户登录的id
        //用户的id是雪花算法自动生成的，主要就是为了解决在分布式的情况下如何实现不生成相同的用户id
        long empId = (long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("用户注册成功");
    }

    //分页查询用户信息：http://localhost/employee/page?page=1&pageSize=10
    //GET方法
    //使用mybatis-plus的分页插件
    @GetMapping("/page")
    public R<Page<Employee>> page(Integer page, Integer pageSize, String name) {
        log.info("page:{} pageSize:{} name:{}", page, pageSize, name);
        //构造分页对象
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加一个排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    //根据id修改员工信息
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("用户信息:{}", employee.toString());
        //获取id
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);
        //调用接口中封装好的更新方法
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }
}
