import Vue from 'vue';
import VueRouter from 'vue-router';
import index from '../views/index.vue';
import login from '../views/login.vue';
import forgot from '../views/forgot.vue';
Vue.use(VueRouter)

const routes = [
    // 主页
    {
        path: '/',
        name: 'index',
        component: index,
        meta: {
            index: 0,
            title: '首页'
        }
    },

    // 登录
    {
        path: '/login',
        name: 'login',
        component: login,
        meta: {
            index: 0,
            title: '登录'
        }
    },

    	
	
    // 忘记密码
    {
        path: '/forgot',
        name: "forgot",
        component: forgot,
        meta: {
            index: 0,
            title: '忘记密码'
        }
    },

    // 修改密码
    {
        path: '/user/password',
        name: "password",
        component: () => import("../views/user/password.vue"),
        meta: {
            index: 0,
            title: '修改密码'
        }
    },

    // 视频播放页
    {
        path: "/media/video",
        name: "video",
        component: () => import('../views/media/video.vue'),
        meta: {
            index: 0,
            title: "视频"
        }
    },

    // 音频播放页
    {
        path: "/media/audio",
        name: "audio",
        component: () => import('../views/media/audio.vue'),
        meta: {
            index: 0,
            title: "音频"
        }
    },

    
    
                // 轮播图路由
        {
            path: '/slides/table',
            name: 'slides_table',
            component: () => import('../views/slides/table.vue'),
            meta: {
                index: 0,
                title: '轮播图列表'
            }
        },
        {
            path: '/slides/view',
            name: 'slides_view',
            component: () => import('../views/slides/view.vue'),
            meta: {
                index: 0,
                title: '轮播图详情'
            }
        },
                            
    
    
            // 公告路由
        {
            path: '/notice/table',
            name: 'notice_table',
            component: () => import('../views/notice/table.vue'),
            meta: {
                index: 0,
                title: '游戏公告列表'
            }
        },
        {
            path: '/notice/view',
            name: 'notice_view',
            component: () => import('../views/notice/view.vue'),
            meta: {
                index: 0,
                title: '游戏公告详情'
            }
        },
            	    
            // 评论路由
        {
            path: '/comment/table',
            name: 'comment_table',
            component: () => import('../views/comment/table.vue'),
            meta: {
                index: 0,
                title: '评论列表'
            }
        },
        {
            path: '/comment/view',
            name: 'comment_view',
            component: () => import('../views/comment/view.vue'),
            meta: {
                index: 0,
                title: '评论详情'
            }
        },
        	            // 游戏玩家路由
        {
            path: '/gamer/table',
            name: 'gamer_table',
            component: () => import('../views/gamer/table.vue'),
            meta: {
                index: 0,
                title: '游戏玩家列表'
            }
        },
        {
            path: '/gamer/view',
            name: 'gamer_view',
            component: () => import('../views/gamer/view.vue'),
            meta: {
                index: 0,
                title: '游戏玩家详情'
            }
        },
						            // 游戏关卡路由
        {
            path: '/game_levels/table',
            name: 'game_levels_table',
            component: () => import('../views/game_levels/table.vue'),
            meta: {
                index: 0,
                title: '游戏关卡列表'
            }
        },
        {
            path: '/game_levels/view',
            name: 'game_levels_view',
            component: () => import('../views/game_levels/view.vue'),
            meta: {
                index: 0,
                title: '游戏关卡详情'
            }
        },
						            // 游戏记录路由
        {
            path: '/game_record/table',
            name: 'game_record_table',
            component: () => import('../views/game_record/table.vue'),
            meta: {
                index: 0,
                title: '游戏记录列表'
            }
        },
        {
            path: '/game_record/view',
            name: 'game_record_view',
            component: () => import('../views/game_record/view.vue'),
            meta: {
                index: 0,
                title: '游戏记录详情'
            }
        },
						            // 道具商店路由
        {
            path: '/props/table',
            name: 'props_table',
            component: () => import('../views/props/table.vue'),
            meta: {
                index: 0,
                title: '道具商店列表'
            }
        },
        {
            path: '/props/view',
            name: 'props_view',
            component: () => import('../views/props/view.vue'),
            meta: {
                index: 0,
                title: '道具商店详情'
            }
        },
						            // 购买记录路由
        {
            path: '/purchase_record/table',
            name: 'purchase_record_table',
            component: () => import('../views/purchase_record/table.vue'),
            meta: {
                index: 0,
                title: '购买记录列表'
            }
        },
        {
            path: '/purchase_record/view',
            name: 'purchase_record_view',
            component: () => import('../views/purchase_record/view.vue'),
            meta: {
                index: 0,
                title: '购买记录详情'
            }
        },
						            // 游戏成就路由
        {
            path: '/game_achievements/table',
            name: 'game_achievements_table',
            component: () => import('../views/game_achievements/table.vue'),
            meta: {
                index: 0,
                title: '游戏成就列表'
            }
        },
        {
            path: '/game_achievements/view',
            name: 'game_achievements_view',
            component: () => import('../views/game_achievements/view.vue'),
            meta: {
                index: 0,
                title: '游戏成就详情'
            }
        },
						            // 玩家成就路由
        {
            path: '/achievements/table',
            name: 'achievements_table',
            component: () => import('../views/achievements/table.vue'),
            meta: {
                index: 0,
                title: '玩家成就列表'
            }
        },
        {
            path: '/achievements/view',
            name: 'achievements_view',
            component: () => import('../views/achievements/view.vue'),
            meta: {
                index: 0,
                title: '玩家成就详情'
            }
        },
						    	    // 用户路由
    {
        path: '/user/table',
        name: 'user_table',
        component: () => import('../views/user/table.vue'),
        meta: {
            index: 0,
            title: '用户列表'
        }
    },
    {
        path: '/user/view',
        name: 'user_view',
        component: () => import('../views/user/view.vue'),
        meta: {
            index: 0,
            title: '用户详情'
        }
    },
    {
        path: '/user/info',
        name: 'user_info',
        component: () => import('../views/user/info.vue'),
        meta: {
            index: 0,
            title: '个人信息'
        }
    },
    // 用户组路由
    {
        path: '/user_group/table',
        name: 'user_group_table',
        component: () => import('../views/user_group/table.vue'),
        meta: {
            index: 0,
            title: '用户组列表'
        }
    },
    {
        path: '/user_group/view',
        name: 'user_group_view',
        component: () => import('../views/user_group/view.vue'),
        meta: {
            index: 0,
            title: '用户组详情'
        }
    }
]

const router = new VueRouter({
    mode: 'hash',
    base: process.env.BASE_URL,
    routes
})

router.beforeEach((to, from, next) => {
    let token = to.query.token;
    if (token) {
        $.db.set("token", token, 120);
    }
    next();
})

router.afterEach((to, from, next) => {
    let title = "开心消消乐游戏-admin";
    document.title = title;
})

export default router
