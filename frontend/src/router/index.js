import { createRouter, createWebHashHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Users from '../views/Users.vue'
import Courses from '../views/Courses.vue'
import Learning from '../views/Learning.vue'
import Agent from '../views/Agent.vue'
import Resources from '../views/Resources.vue'
import Settings from '../views/Settings.vue'
import Help from '../views/Help.vue'
import Recommendation from '../views/Recommendation.vue'
import Clazz from '../views/Clazz.vue'
import Assessment from '../views/Assessment.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/users',
    name: 'Users',
    component: Users
  },
  {
    path: '/courses',
    name: 'Courses',
    component: Courses
  },
  {
    path: '/learning',
    name: 'Learning',
    component: Learning
  },
  {
    path: '/recommendation',
    name: 'Recommendation',
    component: Recommendation
  },
  {
    path: '/clazz',
    name: 'Clazz',
    component: Clazz
  },
  {
    path: '/assessment',
    name: 'Assessment',
    component: Assessment
  },
  {
    path: '/agent',
    name: 'Agent',
    component: Agent
  },
  {
    path: '/resources',
    name: 'Resources',
    component: Resources
  },
  {
    path: '/settings',
    name: 'Settings',
    component: Settings
  },
  {
    path: '/help',
    name: 'Help',
    component: Help
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
