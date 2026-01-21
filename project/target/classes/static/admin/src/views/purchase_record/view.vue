<template>
	<el-main class="bg edit_wrap comtable_e">
		<el-form ref="form" :model="form" status-icon label-width="120px" v-if="is_view()">
		<el-row class="row_ce"> 
							<el-col v-if="$check_field('get','purchase_user') || $check_field('add','purchase_user') || $check_field('set','purchase_user')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="购买用户" prop="purchase_user">
																					<div v-if="user_group !== '管理员'">
							{{ get_user_session_purchase_user(form['purchase_user']) }}
							<el-select v-if="(form['purchase_record_id'] && $check_field('set','purchase_user')) || (!form['purchase_record_id'] && $check_field('add','purchase_user'))" id="purchase_user" v-model="form['purchase_user']" :disabled="disabledObj['purchase_user_isDisabled']">
								<el-option v-for="o in list_user_purchase_user" :key="o['username']" :label="o['nickname'] + '-' + o['username']"
										   :value="o['user_id']">
								</el-option>
							</el-select>
							<el-select v-else-if="$check_field('get','purchase_user')" id="purchase_user" v-model="form['purchase_user']" :disabled="true">
								<el-option v-for="o in list_user_purchase_user" :key="o['username']" :label="o['nickname'] + '-' + o['username']"
										   :value="o['user_id']">
								</el-option>
							</el-select>
						</div>
						<el-select v-else id="purchase_user" v-model="form['purchase_user']" :disabled="disabledObj['purchase_user_isDisabled']">
							<el-option v-for="o in list_user_purchase_user" :key="o['username']" :label="o['nickname'] + '-' + o['username']"
									   :value="o['user_id']">
							</el-option>
						</el-select>
																</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','player_screen_name') || $check_field('add','player_screen_name') || $check_field('set','player_screen_name')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="玩家网名" prop="player_screen_name">
															<el-input id="player_screen_name" v-model="form['player_screen_name']" placeholder="请输入玩家网名"
							  v-if="(form['purchase_record_id'] && $check_field('set','player_screen_name')) || (!form['purchase_record_id'] && $check_field('add','player_screen_name'))" :disabled="disabledObj['player_screen_name_isDisabled']"></el-input>
					<div v-else-if="$check_field('get','player_screen_name')">{{form['player_screen_name']}}</div>
											</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','prop_name') || $check_field('add','prop_name') || $check_field('set','prop_name')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="道具名称" prop="prop_name">
															<el-input id="prop_name" v-model="form['prop_name']" placeholder="请输入道具名称"
							  v-if="(form['purchase_record_id'] && $check_field('set','prop_name')) || (!form['purchase_record_id'] && $check_field('add','prop_name'))" :disabled="disabledObj['prop_name_isDisabled']"></el-input>
					<div v-else-if="$check_field('get','prop_name')">{{form['prop_name']}}</div>
											</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','props_picture') || $check_field('add','props_picture') || $check_field('set','props_picture')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="道具图片" prop="props_picture">
															<el-input id="props_picture" v-model="form['props_picture']" placeholder="请输入道具图片"
							  v-if="(form['purchase_record_id'] && $check_field('set','props_picture')) || (!form['purchase_record_id'] && $check_field('add','props_picture'))" :disabled="disabledObj['props_picture_isDisabled']"></el-input>
					<div v-else-if="$check_field('get','props_picture')">{{form['props_picture']}}</div>
											</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','props_use') || $check_field('add','props_use') || $check_field('set','props_use')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="道具用途" prop="props_use">
															<el-input id="props_use" v-model="form['props_use']" placeholder="请输入道具用途"
							  v-if="(form['purchase_record_id'] && $check_field('set','props_use')) || (!form['purchase_record_id'] && $check_field('add','props_use'))" :disabled="disabledObj['props_use_isDisabled']"></el-input>
					<div v-else-if="$check_field('get','props_use')">{{form['props_use']}}</div>
											</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','gold_coins_required') || $check_field('add','gold_coins_required') || $check_field('set','gold_coins_required')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="所需金币" prop="gold_coins_required">
															<el-input id="gold_coins_required" v-model="form['gold_coins_required']" placeholder="请输入所需金币"
							  v-if="(form['purchase_record_id'] && $check_field('set','gold_coins_required')) || (!form['purchase_record_id'] && $check_field('add','gold_coins_required'))" :disabled="disabledObj['gold_coins_required_isDisabled']"></el-input>
					<div v-else-if="$check_field('get','gold_coins_required')">{{form['gold_coins_required']}}</div>
											</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','use_status') || $check_field('add','use_status') || $check_field('set','use_status')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="使用状态" prop="use_status">
											<el-select id="use_status" v-model="form['use_status']"
						v-if="(form['purchase_record_id'] && $check_field('set','use_status')) || (!form['purchase_record_id'] && $check_field('add','use_status'))">
						<el-option v-for="o in list_use_status" :key="o" :label="o" :value="o">
						</el-option>
					</el-select>
					<div v-else-if="$check_field('get','use_status')">{{form['use_status']}}</div>
							</el-form-item>
			</el-col>
						
	
	
		
		
	
	
	
		
		
	
		</el-row>
			<el-col :xs="24" :sm="12" :lg="8" class="el_form_btn_warp">
				<el-form-item v-if="$check_action('/purchase_record/view','set') || $check_action('/purchase_record/view','add')">
					<el-button type="primary" @click="submit()">提交</el-button>
					<el-button @click="cancel()">取消</el-button>
				</el-form-item>
				<el-form-item v-else>
					<el-button @click="cancel()">返回</el-button>
				</el-form-item>
			</el-col>

		</el-form>
	</el-main>
</template>

<script>
	import mixin from "@/mixins/page.js";
	export default {
		mixins: [mixin],
		data() {
			return {
				field: "purchase_record_id",
				url_add: "~/api/purchase_record/add?",
				url_set: "~/api/purchase_record/set?",
				url_get_obj: "~/api/purchase_record/get_obj?",
				url_upload: "~/api/purchase_record/upload?",

				query: {
					"purchase_record_id": 0,
				},

				form: {
								"purchase_user": 0, // 购买用户
										"player_screen_name":  '', // 玩家网名
										"prop_name":  '', // 道具名称
										"props_picture":  '', // 道具图片
										"props_use":  '', // 道具用途
										"gold_coins_required":  '', // 所需金币
										"use_status":  '', // 使用状态
											"purchase_record_id": 0, // ID
															},
				disabledObj:{
								"purchase_user_isDisabled": false,
										"player_screen_name_isDisabled": false,
										"prop_name_isDisabled": false,
										"props_picture_isDisabled": false,
										"props_use_isDisabled": false,
										"gold_coins_required_isDisabled": false,
										"use_status_isDisabled": false,
										},

	
					// 用户列表
				list_user_purchase_user: [],
						// 用户组
				group_user_purchase_user: "",
					
			
			
			
			
									// 使用状态选项列表
				list_use_status: ['可用','禁用'],
	
		
			}
		},
		methods: {

	
	
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
					/**
			 * 获取游戏玩家用户组
			 */
			async get_group_user_purchase_user() {
							this.form["purchase_user"] = this.user.user_id;
							var json = await this.$get("~/api/user_group/get_obj?name=游戏玩家");
				if(json.result && json.result.obj){
					this.group_user_purchase_user = json.result.obj;
				}
				else if(json.error){
					console.error(json.error);
				}
			},
			get_user_session_purchase_user(id){
				var _this = this;
				var user_id = {"user_id":id}
				var url = "~/api/"+_this.group_user_purchase_user.source_table+"/get_obj?"
				this.$get(url, user_id, function(res) {
					if (res.result && res.result.obj) {
						var arr = []
						for (let key in res.result.obj) {
							arr.push(key)
						}
						var arrForm = []
									for (let key in _this.form) {
							arrForm.push(key)
						}
												_this.form["purchase_user"] = id
									_this.disabledObj['purchase_user' + '_isDisabled'] = true
						for (var i=0;i<arr.length;i++){
						  if (arr[i]!=='examine_state' && arr[i]!=='examine_reply') {
							for (var j = 0; j < arrForm.length; j++) {
							  if (arr[i] === arrForm[j]) {
								if (arr[i] !== "purchase_user") {
			                      _this.form[arrForm[j]] = res.result.obj[arr[i]]
			                      _this.disabledObj[arrForm[j] + '_isDisabled'] = true
								  break;
								} else {
								  _this.disabledObj[arrForm[j] + '_isDisabled'] = true
								}
							  }
							}
						  }
						}
					}
				});
			},
					get_user_purchase_user(id){
				var obj = this.list_user_purchase_user.getObj({"user_id":id});
				var ret = "";
				if(obj){
					if(obj.nickname){
						ret = obj.nickname;}
					else{
						ret = obj.username;
					}
				}
				return ret;
			},
			
	
			
	
			
	
			
	
			
	
			
	
		
			/**
			 * 获取对象之前
			 * @param {Object} param
			 */
			get_obj_before(param) {
				var form = "";
													// 获取缓存数据附加
				form = $.db.get("form");
									$.push(this.form ,form);
												
				if(this.form && form){
					Object.keys(this.form).forEach(key => {
						Object.keys(form).forEach(dbKey => {
							// if(dbKey === "charging_standard"){
							// 	this.form['charging_rules'] = form[dbKey];
							// 	this.disabledObj['charging_rules_isDisabled'] = true;
							// };
							if(key === dbKey){
								this.disabledObj[key+'_isDisabled'] = true;
								this.form[key] = form[dbKey]
							}
							if(dbKey === "source_table"){
								this.form['source_table'] = form[dbKey];
							}
							if(dbKey === "source_id"){
								this.form['source_id'] = form[dbKey];
							}
							if(dbKey === "source_user_id"){
								this.form['source_user_id'] = form[dbKey];
							}
						})
					})
				}
											$.db.del("form");

				return param;
			},

			/**
			 * 获取对象之后
			 * @param {Object} json
			 * @param {Object} func
			 */
			get_obj_after(json, func){
																																			

			},

																																																													async submit(param, func){
				if (!param) {
					param = this.form;
				}
						
				var pm = this.events("submit_before", Object.assign({}, param)) || param;
				var msg = await this.events("submit_check", pm);
				var ret;
				if (msg) {
					this.$toast(msg, 'danger');
				} else {
																																																												ret = this.events("submit_main", pm, func);
				}
				return ret;
			},
			
			/**
			 * 提交前验证事件
			 * @param {Object} 请求参数
			 * @return {String} 验证成功返回null, 失败返回错误提示
			 */
						submit_check(param) {
					
																																																																																			return null;
			},

			is_view(){
				// var bl = this.user_group == "管理员";
				var bl = false;

				if(!bl){
					bl = this.$check_action('/purchase_record/table','add');
					console.log(bl ? "你有表格添加权限视作有添加权限" : "你没有表格添加权限");
				}
				if(!bl){
					bl = this.$check_action('/purchase_record/table','set');
					console.log(bl ? "你有表格添加权限视作有修改权限" : "你没有表格修改权限");
				}
				if(!bl){
					bl = this.$check_action('/purchase_record/view','add');
					console.log(bl ? "你有视图添加权限视作有添加权限" : "你没有视图添加权限");
				}
				if(!bl){
					bl = this.$check_action('/purchase_record/view','set');
					console.log(bl ? "你有视图修改权限视作有修改权限" : "你没有视图修改权限");
				}
				if(!bl){
					bl = this.$check_action('/purchase_record/view','get');
					console.log(bl ? "你有视图查询权限视作有查询权限" : "你没有视图查询权限");
				}

				console.log(bl ? "具有当前页面的查看权，请注意这不代表你有字段的查看权" : "无权查看当前页，请注意即便有字段查询权限没有页面查询权限也不行");

				return bl;
			},
			/**
			 * 上传文件
			 * @param {Object} param
			 */
			uploadimg(param) {
				this.uploadFile(param.file, "avatar");
			},

		},
		created() {
					this.get_list_user_purchase_user();
					this.get_group_user_purchase_user();
																	},
	}
</script>

<style>
	.avatar-uploader .el-upload {
		border: 1px dashed #d9d9d9;
		border-radius: 6px;
		cursor: pointer;
		position: relative;
		overflow: hidden;
	}

	.avatar-uploader .el-upload:hover {
		border-color: #409EFF;
	}

	.avatar-uploader-icon {
		font-size: 28px;
		color: #8c939d;
		width: 178px;
		height: 178px;
		line-height: 178px;
		text-align: center;
	}

	.avatar {
		width: 178px;
		height: 178px;
		display: block;
	}
	
	.img_multiple{
		overflow: hidden;
	}
	.img_multiple .img_block{
		float: left;
		margin-right: 5px;
		margin-bottom: 5px;
		position: relative;
	}
	.img_multiple .img_block img{
		height: 100px;
		width: auto;
	}
	.img_multiple .img_del{
		position: absolute;
		top: 5px;
		right: 5px;
		width: 20px;
		height: 20px;
		background: #0000008a;
		color: #fff;
		line-height: 20px;
		text-align: center;
		border-radius: 100%;
		cursor: pointer;
	}




	
</style>
