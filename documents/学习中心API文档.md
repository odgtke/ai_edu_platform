# 学习中心后端 API 文档

> **版本**: v1.0  
> **基础路径**: `/learning`  
> **服务端口**: 8081

---

## 目录

1. [通用响应格式](#通用响应格式)
2. [学习记录 API](#学习记录-api)
3. [学习进度 API](#学习进度-api)
4. [学习统计 API](#学习统计-api)
5. [学习日历 API](#学习日历-api)
6. [本周学习统计 API](#本周学习统计-api)
7. [数据模型](#数据模型)

---

## 通用响应格式

所有 API 返回统一响应结构：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1711152000000
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 状态码，200 表示成功 |
| message | String | 响应消息 |
| data | Object | 响应数据 |
| timestamp | Long | 时间戳（毫秒） |

---

## 学习记录 API

### 1. 获取学生学习记录

获取指定学生的所有学习记录，包含课程名称等详细信息。

- **URL**: `GET /learning/records/student/{studentId}`
- **Content-Type**: `application/json`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| studentId | Long | 是 | 学生ID，路径参数 |

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "recordId": 1,
      "studentId": 1,
      "courseId": 1,
      "courseName": "Java程序设计",
      "totalLessons": 20,
      "completedLessons": 12,
      "lessonId": null,
      "studyDuration": 120,
      "progress": 60,
      "isCompleted": false,
      "lastStudyTime": "2026-03-20T14:30:00",
      "createTime": "2026-03-15T10:00:00",
      "updateTime": "2026-03-20T14:30:00"
    }
  ],
  "timestamp": 1711152000000
}
```

**字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| recordId | Long | 记录ID |
| studentId | Long | 学生ID |
| courseId | Long | 课程ID |
| courseName | String | 课程名称 |
| totalLessons | Integer | 课程总课时数 |
| completedLessons | Integer | 已学课时数（根据进度计算） |
| lessonId | Long | 课时ID |
| studyDuration | Integer | 学习时长（分钟） |
| progress | Integer | 学习进度百分比（0-100） |
| isCompleted | Boolean | 是否完成 |
| lastStudyTime | String | 最后学习时间（ISO 8601格式） |
| createTime | String | 创建时间 |
| updateTime | String | 更新时间 |

---

### 2. 获取课程学习记录

获取指定课程的所有学习记录。

- **URL**: `GET /learning/records/course/{courseId}`
- **Content-Type**: `application/json`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| courseId | Long | 是 | 课程ID，路径参数 |

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "recordId": 1,
      "studentId": 1,
      "courseId": 1,
      "lessonId": null,
      "studyDuration": 120,
      "progress": 60,
      "isCompleted": false,
      "lastStudyTime": "2026-03-20T14:30:00",
      "createTime": "2026-03-15T10:00:00",
      "updateTime": "2026-03-20T14:30:00"
    }
  ],
  "timestamp": 1711152000000
}
```

---

### 3. 创建或更新学习记录

创建新的学习记录或更新现有记录。

- **URL**: `POST /learning/record`
- **Content-Type**: `application/json`

**请求体**:

```json
{
  "recordId": null,
  "studentId": 1,
  "courseId": 1,
  "lessonId": null,
  "studyDuration": 120,
  "progress": 60,
  "isCompleted": false,
  "lastStudyTime": "2026-03-20T14:30:00"
}
```

**字段说明**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| recordId | Long | 否 | 记录ID，为空时创建新记录 |
| studentId | Long | 是 | 学生ID |
| courseId | Long | 是 | 课程ID |
| lessonId | Long | 否 | 课时ID |
| studyDuration | Integer | 否 | 学习时长（分钟） |
| progress | Integer | 否 | 学习进度百分比（0-100） |
| isCompleted | Boolean | 否 | 是否完成 |
| lastStudyTime | String | 否 | 最后学习时间 |

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": true,
  "timestamp": 1711152000000
}
```

---

## 学习进度 API

### 4. 获取学生某课程的学习进度

获取指定学生在指定课程上的学习进度。

- **URL**: `GET /learning/progress/student/{studentId}/course/{courseId}`
- **Content-Type**: `application/json`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| studentId | Long | 是 | 学生ID，路径参数 |
| courseId | Long | 是 | 课程ID，路径参数 |

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "recordId": 1,
    "studentId": 1,
    "courseId": 1,
    "lessonId": null,
    "studyDuration": 120,
    "progress": 60,
    "isCompleted": false,
    "lastStudyTime": "2026-03-20T14:30:00",
    "createTime": "2026-03-15T10:00:00",
    "updateTime": "2026-03-20T14:30:00"
  },
  "timestamp": 1711152000000
}
```

---

### 5. 获取学生学习进度（综合统计）

获取学生的综合学习进度统计，包含总学习时长、已完成课程数、平均进度、最近学习记录等。

- **URL**: `GET /learning/student/{studentId}/progress`
- **Content-Type**: `application/json`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| studentId | Long | 是 | 学生ID，路径参数 |

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalStudyTime": 480,
    "completedCoursesCount": 2,
    "learningRecordsCount": 5,
    "averageProgress": 58.5,
    "assignmentSubmissionRate": 85.0,
    "recentRecords": [
      {
        "recordId": 1,
        "studentId": 1,
        "courseId": 1,
        "courseName": "Java程序设计",
        "totalLessons": 20,
        "completedLessons": 12,
        "lessonId": null,
        "studyDuration": 120,
        "progress": 60,
        "isCompleted": false,
        "lastStudyTime": "2026-03-20T14:30:00",
        "createTime": "2026-03-15T10:00:00",
        "updateTime": "2026-03-20T14:30:00"
      }
    ]
  },
  "timestamp": 1711152000000
}
```

**字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| totalStudyTime | Integer | 总学习时长（分钟） |
| completedCoursesCount | Long | 已完成课程数 |
| learningRecordsCount | Integer | 学习记录总数 |
| averageProgress | Double | 平均学习进度（%） |
| assignmentSubmissionRate | Double | 作业提交率（%） |
| recentRecords | Array | 最近5条学习记录 |

---

## 学习统计 API

### 6. 获取学生的总体学习统计

获取学生的简化版学习统计数据。

- **URL**: `GET /learning/statistics/student/{studentId}`
- **Content-Type**: `application/json`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| studentId | Long | 是 | 学生ID，路径参数 |

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalStudyTime": 480,
    "completedCoursesCount": 2,
    "learningRecordsCount": 5
  },
  "timestamp": 1711152000000
}
```

---

### 7. 获取班级统计信息

获取指定班级的学习统计信息（当前为模拟数据）。

- **URL**: `GET /learning/class/{classId}/statistics`
- **Content-Type**: `application/json`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| classId | Long | 是 | 班级ID，路径参数 |

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "classId": 1,
    "totalStudents": 25,
    "avgCompletionRate": 78.5,
    "totalCourses": 8,
    "updateTime": "2026-03-23T10:42:00"
  },
  "timestamp": 1711152000000
}
```

---

## 学习日历 API

### 8. 获取学习日历

获取学生最近30天的学习日历数据，按日期统计学习时长。

- **URL**: `GET /learning/student/{studentId}/calendar`
- **Content-Type**: `application/json`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| studentId | Long | 是 | 学生ID，路径参数 |

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "date": "2026-02-21",
      "dayOfMonth": 21,
      "dayOfWeek": 5,
      "studyMinutes": 120,
      "studyHours": 2.0,
      "hasStudy": true
    },
    {
      "date": "2026-02-22",
      "dayOfMonth": 22,
      "dayOfWeek": 6,
      "studyMinutes": 0,
      "studyHours": 0.0,
      "hasStudy": false
    }
  ],
  "timestamp": 1711152000000
}
```

**字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| date | String | 日期（yyyy-MM-dd） |
| dayOfMonth | Integer | 日期中的天数（1-31） |
| dayOfWeek | Integer | 星期几（1=周一，7=周日） |
| studyMinutes | Integer | 学习时长（分钟） |
| studyHours | Double | 学习时长（小时，保留1位小数） |
| hasStudy | Boolean | 是否有学习记录 |

---

## 本周学习统计 API

### 9. 获取本周学习统计

获取学生本周的学习统计数据，包含每日学习时长分布。

- **URL**: `GET /learning/student/{studentId}/weekly`
- **Content-Type**: `application/json`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| studentId | Long | 是 | 学生ID，路径参数 |

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "weeklyStudyMinutes": 480,
    "weeklyStudyHours": 8.0,
    "targetHours": 15,
    "completionRate": 53,
    "dailyBreakdown": {
      "周一": 120,
      "周二": 90,
      "周三": 150,
      "周四": 120,
      "周五": 0,
      "周六": 0,
      "周日": 0
    }
  },
  "timestamp": 1711152000000
}
```

**字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| weeklyStudyMinutes | Integer | 本周总学习时长（分钟） |
| weeklyStudyHours | Double | 本周总学习时长（小时，保留1位小数） |
| targetHours | Integer | 每周目标学习时长（固定15小时） |
| completionRate | Integer | 目标完成率（%） |
| dailyBreakdown | Object | 每日学习时长分布（分钟） |

---

## 数据模型

### LearningRecordDTO 学习记录数据传输对象

```typescript
interface LearningRecordDTO {
  recordId: number;          // 记录ID
  studentId: number;         // 学生ID
  courseId: number;          // 课程ID
  courseName: string;        // 课程名称
  totalLessons: number;      // 课程总课时数
  completedLessons: number;  // 已学课时数
  lessonId: number | null;   // 课时ID
  studyDuration: number;     // 学习时长（分钟）
  progress: number;          // 学习进度百分比（0-100）
  isCompleted: boolean;      // 是否完成
  lastStudyTime: string;     // 最后学习时间（ISO 8601）
  createTime: string;        // 创建时间
  updateTime: string;        // 更新时间
}
```

### LearningRecord 学习记录实体

```typescript
interface LearningRecord {
  recordId: number;          // 记录ID
  studentId: number;         // 学生ID
  courseId: number;          // 课程ID
  lessonId: number | null;   // 课时ID
  studyDuration: number;     // 学习时长（分钟）
  progress: number;          // 学习进度百分比（0-100）
  isCompleted: boolean;      // 是否完成
  lastStudyTime: string;     // 最后学习时间
  createTime: string;        // 创建时间
  updateTime: string;        // 更新时间
}
```

---

## 前端调用示例

### Axios 封装示例

```typescript
import axios from 'axios';

const API_BASE = 'http://localhost:8081';

// 获取学生学习记录
export const getStudentRecords = (studentId: number) => {
  return axios.get(`${API_BASE}/learning/records/student/${studentId}`);
};

// 获取学生学习进度（综合统计）
export const getStudentProgress = (studentId: number) => {
  return axios.get(`${API_BASE}/learning/student/${studentId}/progress`);
};

// 获取学习日历
export const getLearningCalendar = (studentId: number) => {
  return axios.get(`${API_BASE}/learning/student/${studentId}/calendar`);
};

// 获取本周学习统计
export const getWeeklyStatistics = (studentId: number) => {
  return axios.get(`${API_BASE}/learning/student/${studentId}/weekly`);
};

// 创建或更新学习记录
export const saveLearningRecord = (record: LearningRecord) => {
  return axios.post(`${API_BASE}/learning/record`, record);
};
```

### Vue 组件中使用示例

```vue
<script setup>
import { ref, onMounted } from 'vue';
import { getStudentProgress, getLearningCalendar, getWeeklyStatistics } from '@/api/learning';

const studentId = 1;
const progress = ref({});
const calendar = ref([]);
const weeklyStats = ref({});

onMounted(async () => {
  // 并行请求多个接口
  const [progressRes, calendarRes, weeklyRes] = await Promise.all([
    getStudentProgress(studentId),
    getLearningCalendar(studentId),
    getWeeklyStatistics(studentId)
  ]);
  
  progress.value = progressRes.data.data;
  calendar.value = calendarRes.data.data;
  weeklyStats.value = weeklyRes.data.data;
});
</script>
```

---

## 注意事项

1. **时间格式**: 所有时间字段使用 ISO 8601 格式（如 `2026-03-20T14:30:00`）
2. **时长单位**: 学习时长统一使用**分钟**作为单位
3. **进度范围**: 学习进度百分比范围为 0-100
4. **默认课时**: 当课程未设置总课时数时，默认使用 20 课时
5. **目标时长**: 每周目标学习时长固定为 15 小时
6. **日历天数**: 学习日历默认返回最近 30 天的数据
7. **最近记录**: 综合进度接口返回最近 5 条学习记录

---

**文档版本**: v1.0  
**最后更新**: 2026-03-23
