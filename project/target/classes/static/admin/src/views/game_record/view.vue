<template>
	<el-main class="bg edit_wrap comtable_e">
		<el-form ref="form" :model="form" status-icon label-width="120px" v-if="is_view()">
		<el-row class="row_ce"> 
							<el-col v-if="$check_field('get','level_name') || $check_field('add','level_name') || $check_field('set','level_name')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="关卡名称" prop="level_name">
															<el-input id="level_name" v-model="form['level_name']" placeholder="请输入关卡名称"
							  v-if="(form['game_record_id'] && $check_field('set','level_name')) || (!form['game_record_id'] && $check_field('add','level_name'))" :disabled="disabledObj['level_name_isDisabled']"></el-input>
					<div v-else-if="$check_field('get','level_name')">{{form['level_name']}}</div>
											</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','gamer') || $check_field('add','gamer') || $check_field('set','gamer')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="游戏玩家" prop="gamer">
																					<div v-if="user_group !== '管理员'">
							{{ get_user_session_gamer(form['gamer']) }}
							<el-select v-if="(form['game_record_id'] && $check_field('set','gamer')) || (!form['game_record_id'] && $check_field('add','gamer'))" id="gamer" v-model="form['gamer']" :disabled="disabledObj['gamer_isDisabled']">
								<el-option v-for="o in list_user_gamer" :key="o['username']" :label="o['nickname'] + '-' + o['username']"
										   :value="o['user_id']">
								</el-option>
							</el-select>
							<el-select v-else-if="$check_field('get','gamer')" id="gamer" v-model="form['gamer']" :disabled="true">
								<el-option v-for="o in list_user_gamer" :key="o['username']" :label="o['nickname'] + '-' + o['username']"
										   :value="o['user_id']">
								</el-option>
							</el-select>
						</div>
						<el-select v-else id="gamer" v-model="form['gamer']" :disabled="disabledObj['gamer_isDisabled']">
							<el-option v-for="o in list_user_gamer" :key="o['username']" :label="o['nickname'] + '-' + o['username']"
									   :value="o['user_id']">
							</el-option>
						</el-select>
																</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','player_avatar') || $check_field('add','player_avatar') || $check_field('set','player_avatar')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="玩家头像" prop="player_avatar">
											<el-upload :disabled="disabledObj['player_avatar_isDisabled']" class="avatar-uploader" drag
						accept="image/gif, image/jpeg, image/png, image/jpg" action="" :http-request="upload_player_avatar"
						:show-file-list="false" v-if="(form['game_record_id'] && $check_field('set','player_avatar')) || (!form['game_record_id'] && $check_field('add','player_avatar'))">
						<img id="player_avatar" v-if="form['player_avatar']" :src="$fullUrl(form['player_avatar'])" class="avatar">
						<i v-else class="el-icon-plus avatar-uploader-icon"></i>
					</el-upload>
					<el-image v-else-if="$check_field('get','player_avatar')" style="width: 100px; height: 100px"
						:src="$fullUrl(form['player_avatar'])" :preview-src-list="[$fullUrl(form['player_avatar'])]">
						<div slot="error" class="image-slot">
							<img src="../../../public/img/error.png" style="width: 90px; height: 90px" />
						</div>
					</el-image>
							</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','player_screen_name') || $check_field('add','player_screen_name') || $check_field('set','player_screen_name')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="玩家网名" prop="player_screen_name">
															<el-input id="player_screen_name" v-model="form['player_screen_name']" placeholder="请输入玩家网名"
							  v-if="(form['game_record_id'] && $check_field('set','player_screen_name')) || (!form['game_record_id'] && $check_field('add','player_screen_name'))" :disabled="disabledObj['player_screen_name_isDisabled']"></el-input>
					<div v-else-if="$check_field('get','player_screen_name')">{{form['player_screen_name']}}</div>
											</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','gold_coin_rewards') || $check_field('add','gold_coin_rewards') || $check_field('set','gold_coin_rewards')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="金币奖励" prop="gold_coin_rewards">
											<el-input-number id="gold_coin_rewards" v-model.number="form['gold_coin_rewards']"
						v-if="(form['game_record_id'] && $check_field('set','gold_coin_rewards')) || (!form['game_record_id'] && $check_field('add','gold_coin_rewards'))" :disabled="disabledObj['gold_coin_rewards_isDisabled']"></el-input-number>
					<div v-else-if="$check_field('get','gold_coin_rewards')">{{form['gold_coin_rewards']}}</div>
							</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','game_time') || $check_field('add','game_time') || $check_field('set','game_time')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="游戏时间" prop="game_time">
											<el-input-number id="game_time" v-model.number="form['game_time']"
						v-if="(form['game_record_id'] && $check_field('set','game_time')) || (!form['game_record_id'] && $check_field('add','game_time'))" :disabled="disabledObj['game_time_isDisabled']"></el-input-number>
					<div v-else-if="$check_field('get','game_time')">{{form['game_time']}}</div>
							</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','number_of_steps_used') || $check_field('add','number_of_steps_used') || $check_field('set','number_of_steps_used')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="使用步数" prop="number_of_steps_used">
											<el-input-number id="number_of_steps_used" v-model.number="form['number_of_steps_used']"
						v-if="(form['game_record_id'] && $check_field('set','number_of_steps_used')) || (!form['game_record_id'] && $check_field('add','number_of_steps_used'))" :disabled="disabledObj['number_of_steps_used_isDisabled']"></el-input-number>
					<div v-else-if="$check_field('get','number_of_steps_used')">{{form['number_of_steps_used']}}</div>
							</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','total_game_score') || $check_field('add','total_game_score') || $check_field('set','total_game_score')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="游戏总分" prop="total_game_score">
											<el-input-number id="total_game_score" v-model.number="form['total_game_score']"
						v-if="(form['game_record_id'] && $check_field('set','total_game_score')) || (!form['game_record_id'] && $check_field('add','total_game_score'))" :disabled="disabledObj['total_game_score_isDisabled']"></el-input-number>
					<div v-else-if="$check_field('get','total_game_score')">{{form['total_game_score']}}</div>
							</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','clearance_status') || $check_field('add','clearance_status') || $check_field('set','clearance_status')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="通关状态" prop="clearance_status">
											<el-select id="clearance_status" v-model="form['clearance_status']"
						v-if="(form['game_record_id'] && $check_field('set','clearance_status')) || (!form['game_record_id'] && $check_field('add','clearance_status'))">
						<el-option v-for="o in list_clearance_status" :key="o" :label="o" :value="o">
						</el-option>
					</el-select>
					<div v-else-if="$check_field('get','clearance_status')">{{form['clearance_status']}}</div>
							</el-form-item>
			</el-col>
						
	
	
		
		
	
	
	
		
		
	
		</el-row>
			<el-col :xs="24" :sm="12" :lg="8" class="el_form_btn_warp">
				<el-form-item v-if="$check_action('/game_record/view','set') || $check_action('/game_record/view','add')">
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
				field: "game_record_id",
				url_add: "~/api/game_record/add?",
				url_set: "~/api/game_record/set?",
				url_get_obj: "~/api/game_record/get_obj?",
				url_upload: "~/api/game_record/upload?",

				query: {
					"game_record_id": 0,
				},

				form: {
								"level_name":  '', // 关卡名称
										"gamer": 0, // 游戏玩家
										"player_avatar":  '', // 玩家头像
										"player_screen_name":  '', // 玩家网名
										"gold_coin_rewards":  0, // 金币奖励
										"game_time":  0, // 游戏时间
										"number_of_steps_used":  0, // 使用步数
										"total_game_score":  0, // 游戏总分
										"clearance_status":  '', // 通关状态
											"game_record_id": 0, // ID
															},
				disabledObj:{
								"level_name_isDisabled": false,
										"gamer_isDisabled": false,
										"player_avatar_isDisabled": false,
										"player_screen_name_isDisabled": false,
					          			"gold_coin_rewards_isDisabled": false,
					          			"game_time_isDisabled": false,
					          			"number_of_steps_used_isDisabled": false,
					          			"total_game_score_isDisabled": false,
										"clearance_status_isDisabled": false,
										},

	
			
					// 用户列表
				list_user_gamer: [],
						// 用户组
				group_user_gamer: "",
					
			
			
			
			
			
									// 通关状态选项列表
				list_clearance_status: ['成功','失败'],
	
		
			}
		},
		methods: {

	
	
			
	
				/**
			 * 获取游戏玩家用户列表
			 */
			async get_list_user_gamer() {
                var json = await this.$get("~/api/user/get_list?user_group=游戏玩家");
                if(json.result && json.result.list){
                    this.list_user_gamer = json.result.list;
                }
                else if(json.error){
                    console.error(json.error);
                }
			},
					/**
			 * 获取游戏玩家用户组
			 */
			async get_group_user_gamer() {
							this.form["gamer"] = this.user.user_id;
							var json = await this.$get("~/api/user_group/get_obj?name=游戏玩家");
				if(json.result && json.result.obj){
					this.group_user_gamer = json.result.obj;
				}
				else if(json.error){
					console.error(json.error);
				}
			},
			get_user_session_gamer(id){
				var _this = this;
				var user_id = {"user_id":id}
				var url = "~/api/"+_this.group_user_gamer.source_table+"/get_obj?"
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
												_this.form["gamer"] = id
									_this.disabledObj['gamer' + '_isDisabled'] = true
						for (var i=0;i<arr.length;i++){
						  if (arr[i]!=='examine_state' && arr[i]!=='examine_reply') {
							for (var j = 0; j < arrForm.length; j++) {
							  if (arr[i] === arrForm[j]) {
								if (arr[i] !== "gamer") {
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
					get_user_gamer(id){
				var obj = this.list_user_gamer.getObj({"user_id":id});
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
			 * 上传玩家头像
			 * @param {Object} param 图片参数
			 */
			upload_player_avatar(param){
									this.uploadFile(param.file, "player_avatar");
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
					bl = this.$check_action('/game_record/table','add');
					console.log(bl ? "你有表格添加权限视作有添加权限" : "你没有表格添加权限");
				}
				if(!bl){
					bl = this.$check_action('/game_record/table','set');
					console.log(bl ? "你有表格添加权限视作有修改权限" : "你没有表格修改权限");
				}
				if(!bl){
					bl = this.$check_action('/game_record/view','add');
					console.log(bl ? "你有视图添加权限视作有添加权限" : "你没有视图添加权限");
				}
				if(!bl){
					bl = this.$check_action('/game_record/view','set');
					console.log(bl ? "你有视图修改权限视作有修改权限" : "你没有视图修改权限");
				}
				if(!bl){
					bl = this.$check_action('/game_record/view','get');
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
							this.get_list_user_gamer();
					this.get_group_user_gamer();
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
