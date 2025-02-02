import Vue from 'vue';
import Router from 'vue-router';
import Store from '@/store';

import LoginView from '@/views/LoginView.vue';
import CourseSelectionView from '@/views/CourseSelectionView.vue';

import HomeView from '@/views/HomeView.vue';
import ManagementView from '@/views/teacher/ManagementView.vue';
import QuestionsView from '@/views/teacher/questions/QuestionsView.vue';
import TopicsView from '@/views/teacher/TopicsView.vue';
import QuizzesView from '@/views/teacher/quizzes/QuizzesView.vue';
import StudentsView from '@/views/teacher/students/StudentsView.vue';
import StudentView from '@/views/student/StudentView.vue';
import AvailableQuizzesView from './views/student/AvailableQuizzesView.vue';
import SolvedQuizzesView from './views/student/SolvedQuizzesView.vue';
import QuizView from './views/student/quiz/QuizView.vue';
import ResultsView from './views/student/quiz/ResultsView.vue';
import StatsView from './views/student/StatsView.vue';
import ScanView from './views/student/ScanView.vue';
import SubmittedQuestionsView from './views/student/submissions/SubmittedQuestionsView.vue';
import SubmittedDashboard from './views/student/submissions/SubmittedDashboard.vue';

import CreateTourney from './views/student/tourney/CreateTourney.vue';
import OpenTourneys from './views/student/tourney/OpenTourneys.vue';
import TourneysDashboard from './views/student/tourney/TourneysDashboard.vue';
import DiscussionAnswerView from './views/student/quiz/DiscussionAnswerView.vue';



import AllDiscussionView from './views/student/quiz/AllDiscussionView.vue';
import DiscussionDashboard from './views/student/DiscussionDashboard.vue';
import DiscussionPublicQ from './views/student/quiz/DiscussionPublicQ.vue';
import TeacherDiscussionView from './views/teacher/TeacherDiscussionView.vue';

import AdminManagementView from '@/views/admin/AdminManagementView.vue';
import NotFoundView from '@/views/NotFoundView.vue';
import ImpExpView from '@/views/teacher/impexp/ImpExpView.vue';
import AssessmentsView from '@/views/teacher/assessments/AssessmentsView.vue';
import CreateQuizzesView from '@/views/student/CreateQuizzesView.vue';
import CoursesView from '@/views/admin/Courses/CoursesView.vue';
import StudentQuestionsView from '@/views/teacher/submitted/StudentQuestionsView.vue';

Vue.use(Router);

let router = new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { title: process.env.VUE_APP_NAME, requiredAuth: 'None' }
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: {
        title: process.env.VUE_APP_NAME + ' - Login',
        requiredAuth: 'None'
      }
    },
    {
      path: '/courses',
      name: 'courses',
      component: CourseSelectionView,
      meta: {
        title: process.env.VUE_APP_NAME + ' - Course Selection',
        requiredAuth: 'None'
      }
    },
    {
      path: '/management',
      name: 'management',
      component: ManagementView,
      children: [
        {
          path: 'questions',
          name: 'questions-management',
          component: QuestionsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Questions',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'topics',
          name: 'topics-management',
          component: TopicsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Topics',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'quizzes',
          name: 'quizzes-management',
          component: QuizzesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Quizzes',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'assessments',
          name: 'assessments-management',
          component: AssessmentsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Assessment Topics',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'students',
          name: 'students-management',
          component: StudentsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Students',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'discussions',
          name: 'discussions',
          component: TeacherDiscussionView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Discussions',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'submitted',
          name: 'student-questions',
          component: StudentQuestionsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Submitted',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'impexp',
          name: 'impexp-management',
          component: ImpExpView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - ImpExp',
            requiredAuth: 'Teacher'
          }
        }
      ]
    },
    {
      path: '/student',
      name: 'student',
      component: StudentView,
      children: [
        {
          path: 'available',
          name: 'available-quizzes',
          component: AvailableQuizzesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Available Quizzes',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'create',
          name: 'create-quizzes',
          component: CreateQuizzesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Create Quizzes',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'solved',
          name: 'solved-quizzes',
          component: SolvedQuizzesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Solved Quizzes',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'quiz',
          name: 'solve-quiz',
          component: QuizView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - quiz',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'results',
          name: 'quiz-results',
          component: ResultsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Results',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'stats',
          name: 'stats',
          component: StatsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Stats',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'scan',
          name: 'scan',
          component: ScanView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Scan',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'discussions',
          name: 'discussions',
          component: AllDiscussionView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Discussions',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'discussions/stats',
          name: 'discussionsStats',
          component: DiscussionDashboard,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Discussions',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'publicQuizDiscussions',
          name: 'public-discussions',
          component: DiscussionPublicQ,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Public Discussions',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'submit',
          name: 'submit',
          component: SubmittedQuestionsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Submit',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'tourneys/create',
          name: 'create-tourney',
          component: CreateTourney,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Create Tourney',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'tourneys',
          name: 'open-tourney',
          component: OpenTourneys,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Open Tourneys',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'studentQuestionsDashboard',
          name: 'student-dashboard',
          component: SubmittedDashboard,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Student Dashboard',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'tourneys/dashboard',
          name: 'tourneys-dashboard',
          component: TourneysDashboard,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Tourneys Dashboard',
            requiredAuth: 'Student'
          }
        }
      ]
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminManagementView,
      children: [
        {
          path: 'courses',
          name: 'courseAdmin',
          component: CoursesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Manage Courses',
            requiredAuth: 'Admin'
          }
        }
      ]
    },
    {
      path: '**',
      name: 'not-found',
      component: NotFoundView,
      meta: { title: 'Page Not Found', requiredAuth: 'None' }
    }
  ]
});

router.beforeEach(async (to, from, next) => {
  if (to.meta.requiredAuth == 'None') {
    next();
  } else if (to.meta.requiredAuth == 'Admin' && Store.getters.isAdmin) {
    next();
  } else if (to.meta.requiredAuth == 'Teacher' && Store.getters.isTeacher) {
    next();
  } else if (to.meta.requiredAuth == 'Student' && Store.getters.isStudent) {
    next();
  } else {
    next('/');
  }
});

router.afterEach(async (to, from) => {
  document.title = to.meta.title;
  await Store.dispatch('clearLoading');
});

export default router;
