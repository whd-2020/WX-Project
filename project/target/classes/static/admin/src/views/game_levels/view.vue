<template>
	<el-main class="bg edit_wrap comtable_e">
		<el-form ref="form" :model="form" status-icon label-width="120px" v-if="is_view()">
		<el-row class="row_ce"> 
							<el-col v-if="$check_field('get','level_name') || $check_field('add','level_name') || $check_field('set','level_name')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="关卡名称" prop="level_name">
															<el-input id="level_name" v-model="form['level_name']" placeholder="请输入关卡名称"
							  v-if="(form['game_levels_id'] && $check_field('set','level_name')) || (!form['game_levels_id'] && $check_field('add','level_name'))" :disabled="disabledObj['level_name_isDisabled']"></el-input>
					<div v-else-if="$check_field('get','level_name')">{{form['level_name']}}</div>
											</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','level_picture') || $check_field('add','level_picture') || $check_field('set','level_picture')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="关卡图片" prop="level_picture">
											<el-upload :disabled="disabledObj['level_picture_isDisabled']" class="avatar-uploader" drag
						accept="image/gif, image/jpeg, image/png, image/jpg" action="" :http-request="upload_level_picture"
						:show-file-list="false" v-if="(form['game_levels_id'] && $check_field('set','level_picture')) || (!form['game_levels_id'] && $check_field('add','level_picture'))">
						<img id="level_picture" v-if="form['level_picture']" :src="$fullUrl(form['level_picture'])" class="avatar">
						<i v-else class="el-icon-plus avatar-uploader-icon"></i>
					</el-upload>
					<el-image v-else-if="$check_field('get','level_picture')" style="width: 100px; height: 100px"
						:src="$fullUrl(form['level_picture'])" :preview-src-list="[$fullUrl(form['level_picture'])]">
						<div slot="error" class="image-slot">
							<img src="../../../public/img/error.png" style="width: 90px; height: 90px" />
						</div>
					</el-image>
							</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','limit_number_of_steps') || $check_field('add','limit_number_of_steps') || $check_field('set','limit_number_of_steps')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="限制步数" prop="limit_number_of_steps">
											<el-input-number id="limit_number_of_steps" v-model.number="form['limit_number_of_steps']"
						v-if="(form['game_levels_id'] && $check_field('set','limit_number_of_steps')) || (!form['game_levels_id'] && $check_field('add','limit_number_of_steps'))" :disabled="disabledObj['limit_number_of_steps_isDisabled']"></el-input-number>
					<div v-else-if="$check_field('get','limit_number_of_steps')">{{form['limit_number_of_steps']}}</div>
							</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','elimination_quantity') || $check_field('add','elimination_quantity') || $check_field('set','elimination_quantity')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="消除数量" prop="elimination_quantity">
											<el-input-number id="elimination_quantity" v-model.number="form['elimination_quantity']"
						v-if="(form['game_levels_id'] && $check_field('set','elimination_quantity')) || (!form['game_levels_id'] && $check_field('add','elimination_quantity'))" :disabled="disabledObj['elimination_quantity_isDisabled']"></el-input-number>
					<div v-else-if="$check_field('get','elimination_quantity')">{{form['elimination_quantity']}}</div>
							</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','game_duration') || $check_field('add','game_duration') || $check_field('set','game_duration')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="游戏时长" prop="game_duration">
											<el-input-number id="game_duration" v-model.number="form['game_duration']"
						v-if="(form['game_levels_id'] && $check_field('set','game_duration')) || (!form['game_levels_id'] && $check_field('add','game_duration'))" :disabled="disabledObj['game_duration_isDisabled']"></el-input-number>
					<div v-else-if="$check_field('get','game_duration')">{{form['game_duration']}}</div>
							</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','gold_coin_rewards') || $check_field('add','gold_coin_rewards') || $check_field('set','gold_coin_rewards')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="金币奖励" prop="gold_coin_rewards">
											<el-input-number id="gold_coin_rewards" v-model.number="form['gold_coin_rewards']"
						v-if="(form['game_levels_id'] && $check_field('set','gold_coin_rewards')) || (!form['game_levels_id'] && $check_field('add','gold_coin_rewards'))" :disabled="disabledObj['gold_coin_rewards_isDisabled']"></el-input-number>
					<div v-else-if="$check_field('get','gold_coin_rewards')">{{form['gold_coin_rewards']}}</div>
							</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','level_description') || $check_field('add','level_description') || $check_field('set','level_description')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="关卡说明" prop="level_description">
											<el-input type="textarea" id="level_description" v-model="form['level_description']" placeholder="请输入关卡说明"
						v-if="(form['game_levels_id'] && $check_field('set','level_description')) || (!form['game_levels_id'] && $check_field('add','level_description'))" :disabled="disabledObj['level_description_isDisabled']"></el-input>
					<div v-else-if="$check_field('get','level_description')">{{form['level_description']}}</div>
							</el-form-item>
			</el-col>
						
	
	
		
		
						<el-col :xs="24" :sm="12" :lg="8" class="el_form_item_warp" v-if="!form['source_user_id'] && 0 > 0">
			  <el-form-item label="开始游戏限次">
				<el-input id="limit_times" v-model="form['game_record_limit_times']" placeholder="开始游戏限制次数，0为不限"
						  v-if="$check_option('/game_levels/view','can_limits')"
				></el-input>
				<div v-else-if="$check_action('/game_levels/view','get')" v-html="form['game_record_limit_times']"></div>
			  </el-form-item>
			</el-col>
			
	
	
		
		
	
		</el-row>
			<el-col :xs="24" :sm="12" :lg="8" class="el_form_btn_warp">
				<el-form-item v-if="$check_action('/game_levels/view','set') || $check_action('/game_levels/view','add')">
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
				field: "game_levels_id",
				url_add: "~/api/game_levels/add?",
				url_set: "~/api/game_levels/set?",
				url_get_obj: "~/api/game_levels/get_obj?",
				url_upload: "~/api/game_levels/upload?",

				query: {
					"game_levels_id": 0,
				},

				form: {
								"level_name":  '', // 关卡名称
										"level_picture":  '', // 关卡图片
										"limit_number_of_steps":  0, // 限制步数
										"elimination_quantity":  0, // 消除数量
										"game_duration":  0, // 游戏时长
										"gold_coin_rewards":  0, // 金币奖励
										"level_description":  '', // 关卡说明
											"game_levels_id": 0, // ID
													"game_record_limit_times": 0, // 开始游戏限制次数
												},
				disabledObj:{
								"level_name_isDisabled": false,
										"level_picture_isDisabled": false,
					          			"limit_number_of_steps_isDisabled": false,
					          			"elimination_quantity_isDisabled": false,
					          			"game_duration_isDisabled": false,
					          			"gold_coin_rewards_isDisabled": false,
										"level_description_isDisabled": false,
										},

	
			
			
			
			
			
			
		
			}
		},
		methods: {

	
	
						/**
			 * 上传关卡图片
			 * @param {Object} param 图片参数
			 */
			upload_level_picture(param){
									this.uploadFile(param.file, "level_picture");
								},
	
	
			
	
			
	
			
	
			
	
			
	
		
			/**
			 * 获取对象之前
			 * @param {Object} param
			 */
			get_obj_before(param) {
				var form = "";
																
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
					bl = this.$check_action('/game_levels/table','add');
					console.log(bl ? "你有表格添加权限视作有添加权限" : "你没有表格添加权限");
				}
				if(!bl){
					bl = this.$check_action('/game_levels/table','set');
					console.log(bl ? "你有表格添加权限视作有修改权限" : "你没有表格修改权限");
				}
				if(!bl){
					bl = this.$check_action('/game_levels/view','add');
					console.log(bl ? "你有视图添加权限视作有添加权限" : "你没有视图添加权限");
				}
				if(!bl){
					bl = this.$check_action('/game_levels/view','set');
					console.log(bl ? "你有视图修改权限视作有修改权限" : "你没有视图修改权限");
				}
				if(!bl){
					bl = this.$check_action('/game_levels/view','get');
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
