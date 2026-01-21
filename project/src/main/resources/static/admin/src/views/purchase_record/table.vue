<template>
	<el-main class="bg table_wrap comtabel_t">
		<el-form label-position="right" :model="query" class="form p_4" label-width="120">
			<el-row class="rows row1">



										<el-col :xs="24" :sm="24" :lg="8" class="el_form_search_wrap">
					<el-form-item label="玩家网名">
									<el-input v-model="query.player_screen_name"></el-input>
								</el-form-item>
				</el-col>
									<el-col :xs="24" :sm="24" :lg="8" class="el_form_search_wrap">
					<el-form-item label="道具名称">
									<el-input v-model="query.prop_name"></el-input>
								</el-form-item>
				</el-col>
																</el-row>
	<el-row class="rows row2">

		<el-col :xs="24" :sm="24" :lg="24" class="search_btn_wrap search_btns">

				<el-col :xs="24" :sm="10" :lg="8" class="search_btn_1 search_btn_wrap_1 btns">

										<el-button type="primary" @click="search()" class="search_btn_find">查询</el-button>
						<el-button @click="reset()" style="margin-right: 74px;" class="search_btn_reset">重置</el-button>
																		

						<el-button v-if="$check_action('/purchase_record/table','del') || $check_action('/purchase_record/view','del')" class="search_btn_del" type="danger" @click="delInfo()">删除</el-button>
								
				</el-col>
		</el-col>
	</el-row >

		</el-form>
				<el-table :data="list" @selection-change="selectionChange" @sort-change="$sortChange" style="width: 100%" id="dataTable">
					<el-table-column fixed type="selection" tooltip-effect="dark" width="55">
			</el-table-column>
				<el-table-column prop="purchase_user" @sort-change="$sortChange" label="购买用户" 				v-if="$check_field('get','purchase_user')" min-width="200">
						<template slot-scope="scope">
					{{ get_user_purchase_user(scope.row['purchase_user']) }}
				</template>
					</el-table-column>
					<el-table-column prop="player_screen_name" @sort-change="$sortChange" label="玩家网名" 				v-if="$check_field('get','player_screen_name')" min-width="200">
					</el-table-column>
					<el-table-column prop="prop_name" @sort-change="$sortChange" label="道具名称" 				v-if="$check_field('get','prop_name')" min-width="200">
					</el-table-column>
					<el-table-column prop="props_picture" @sort-change="$sortChange" label="道具图片" 				v-if="$check_field('get','props_picture')" min-width="200">
					</el-table-column>
					<el-table-column prop="props_use" @sort-change="$sortChange" label="道具用途" 				v-if="$check_field('get','props_use')" min-width="200">
					</el-table-column>
					<el-table-column prop="gold_coins_required" @sort-change="$sortChange" label="所需金币" 				v-if="$check_field('get','gold_coins_required')" min-width="200">
					</el-table-column>
					<el-table-column prop="use_status" @sort-change="$sortChange" label="使用状态" 				v-if="$check_field('get','use_status')" min-width="200">
					</el-table-column>
	


            <el-table-column sortable prop="create_time" label="创建时间" min-width="200">
                <template slot-scope="scope">
                	{{ $toTime(scope.row["create_time"],"yyyy-MM-dd hh:mm:ss") }}
                </template>
            </el-table-column>

			<el-table-column sortable prop="update_time" label="更新时间" min-width="200">
                <template slot-scope="scope">
                	{{ $toTime(scope.row["update_time"],"yyyy-MM-dd hh:mm:ss") }}
                </template>
			</el-table-column>







			<el-table-column fixed="right" label="操作" min-width="200" v-if="$check_action('/purchase_record/table','set') || $check_action('/purchase_record/view','set') || $check_action('/purchase_record/view','get') 
						" >


				<template slot-scope="scope">
					<div class="view_a">
					<router-link class="e-button el-button--small is-plain el-button--success" style="margin: 5px !important;"
					v-if="$check_action('/purchase_record/table','set') || $check_action('/purchase_record/view','set') || $check_action('/purchase_record/view','get')"
						:to="'./view?' + field + '=' + scope.row[field]"
						 size="small">
						<span>详情</span>
					</router-link>
				</div>
				</template>
			</el-table-column>

		</el-table>

		<!-- 分页器 -->
		<div class="mt text_center">
			<el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
				:current-page="query.page" :page-sizes="[7, 10, 30, 100]" :page-size="query.size"
				layout="total, sizes, prev, pager, next, jumper" :total="count">
			</el-pagination>
		</div>
		<!-- /分页器 -->
							
		<div class="modal_wrap" v-if="showModal">
			<div class="modal_box">
				<!-- <div class="modal_box_close" @click="closeModal">X</div> -->
				<p class="modal_box_title">重要提醒</p>
				<p class="modal_box_text">当前有数据达到预警值！</p>
				<p class="modal_box_text">{{ message }}</p>
				<div class="btn_box">
					<span @click="closeModal">取消</span>
					<span @click="closeModal">确定</span>
				</div>
			</div>
		</div>


	</el-main>
</template>
<script>
	import mixin from "@/mixins/page.js";

	export default {
		mixins: [mixin],
		data() {
			return {
				// 弹框
				showModal: false,
				// 获取数据地址
				url_get_list: "~/api/purchase_record/get_list?like=0",
				url_del: "~/api/purchase_record/del?",

				// 字段ID
				field: "purchase_record_id",

				// 查询
				query: {
					"size": 7,
					"page": 1,
									"player_screen_name": "",
											"prop_name": "",
												"login_time": "",
					"create_time": "",
					"orderby": `create_time desc`
				},

				// 数据
				list: [],
															// 用户列表
				list_user_purchase_user: [],
																	message: '',
			}
		},
		methods: {
			// 关闭弹框
			closeModal(){
				this.showModal = false;
				},
			/**
			 * @description 获取到列表事件
			 * @param {Object} res 响应结果
			 */
			get_list_after: function get_list_after(res, func, url) {
				let _this = this
																																										},
			get_list_before(param){
				var user_group = this.user.user_group;
				if(user_group != "管理员"){
						let sqlwhere = "(";
										if(user_group=="游戏玩家"){
						sqlwhere+= "purchase_user = " + this.user.user_id + " or ";
					}
																																								if (sqlwhere.length>1){
						sqlwhere = sqlwhere.substr(0,sqlwhere.length-4);
						sqlwhere += ")";
						param["sqlwhere"] = sqlwhere;
					}
					}
				return param;
			},

														



				/**
			 * 获取游戏玩家用户列表
			 */
			async get_list_user_purchase_user() {
				var json = await this.$get("~/api/user/get_list?user_group=游戏玩家");
				if(json.result && json.result.list){
					this.list_user_purchase_user = json.result.list;
				}
				else if(json.error){
					console.error(json.error);
				}
			},

			get_user_purchase_user(id){
				var obj = this.list_user_purchase_user.getObj({"user_id":id});
				var ret = "";
				if(obj){
					ret = obj.nickname+"-"+obj.username;
					// if(obj.nickname){
					// 	ret = obj.nickname;
					// }
					// else{
					// 	ret = obj.username;
					// }
				}
				return ret;
			},
																},
				created() {
							this.get_list_user_purchase_user();
															}
	}
</script>

<style type="text/css">
	.bg {
		background: white;
	}

	.form.p_4 {
		padding: 1rem;
	}

	.form .el-input {
		width: initial;
	}

	.mt {
		margin-top: 1rem;
	}

	.text_center {
		text-align: center;
	}

	.float-right {
		float: right;
	}


	.modal_wrap{
		width: 100vw;
		height: 100vh;
		position: fixed;
		top: 0;
		left: 0;
		background: rgba(0,0,0,0.5);
		z-index: 9999999999;
	}
	.modal_wrap .modal_box{
		width: 400px;
		height: auto;
		background: url("../../assets/modal_bg.jpg") no-repeat center;
		background-size: cover;
		position: absolute;
		top: 50%;
		left: 50%;
		margin-left: -200px;
		margin-top: -100px;
		border-radius: 10px;
		padding: 10px;
		}
	.modal_wrap .modal_box .modal_box_close{
		font-size: 20px;
		position: absolute;
		top: 10px;
		right: 10px;
		cursor: pointer;
		}
	.modal_wrap .modal_box .modal_box_title{
	  text-align: center;
    font-size: 18px;
    margin: 16px auto;
    color: #fff;
    border-bottom: 1px solid rgba(117, 116, 116,0.5);
    padding-bottom: 16px;
    width: 356px;
		}
	.modal_wrap .modal_box .modal_box_text{
		text-align: center;
		font-size: 14px;
		color: #fff;
		margin: 5px auto;
		width: 90%;
		}
	.modal_wrap .modal_box .btn_box{
		display: flex;
		flex-direction: row;
		justify-content: center;
		margin-top: 42px;
		margin-bottom: 20px;
		}
			.modal_wrap .modal_box .btn_box span{
				display: inline-block;
				width: 80px;
				height: 30px;
				line-height: 30px;
				text-align: center;
				border: 1px solid #ccc;
				font-size: 14px;
				cursor: pointer;
				color: #fff;
			}
	.modal_wrap .modal_box .btn_box span:nth-child(2){
		background: #409EFF;
		color: #fff;
		border-color: #409EFF;
		margin-left: 15px;
	}
	.el-date-editor .el-range-separator{
		width: 10% !important;
	}
</style>
