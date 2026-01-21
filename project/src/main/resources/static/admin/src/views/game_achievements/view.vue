<template>
	<el-main class="bg edit_wrap comtable_e">
		<el-form ref="form" :model="form" status-icon label-width="120px" v-if="is_view()">
		<el-row class="row_ce"> 
							<el-col v-if="$check_field('get','achievement_name') || $check_field('add','achievement_name') || $check_field('set','achievement_name')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="成就名称" prop="achievement_name">
															<el-input id="achievement_name" v-model="form['achievement_name']" placeholder="请输入成就名称"
							  v-if="(form['game_achievements_id'] && $check_field('set','achievement_name')) || (!form['game_achievements_id'] && $check_field('add','achievement_name'))" :disabled="disabledObj['achievement_name_isDisabled']"></el-input>
					<div v-else-if="$check_field('get','achievement_name')">{{form['achievement_name']}}</div>
											</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','type_of_achievement') || $check_field('add','type_of_achievement') || $check_field('set','type_of_achievement')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="成就类型" prop="type_of_achievement">
											<el-select id="type_of_achievement" v-model="form['type_of_achievement']"
						v-if="(form['game_achievements_id'] && $check_field('set','type_of_achievement')) || (!form['game_achievements_id'] && $check_field('add','type_of_achievement'))">
						<el-option v-for="o in list_type_of_achievement" :key="o" :label="o" :value="o">
						</el-option>
					</el-select>
					<div v-else-if="$check_field('get','type_of_achievement')">{{form['type_of_achievement']}}</div>
							</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','score_settings') || $check_field('add','score_settings') || $check_field('set','score_settings')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="分数设置" prop="score_settings">
											<el-input-number id="score_settings" v-model.number="form['score_settings']"
						v-if="(form['game_achievements_id'] && $check_field('set','score_settings')) || (!form['game_achievements_id'] && $check_field('add','score_settings'))" :disabled="disabledObj['score_settings_isDisabled']"></el-input-number>
					<div v-else-if="$check_field('get','score_settings')">{{form['score_settings']}}</div>
							</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','gold_coin_rewards') || $check_field('add','gold_coin_rewards') || $check_field('set','gold_coin_rewards')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="金币奖励" prop="gold_coin_rewards">
															<el-input id="gold_coin_rewards" v-model="form['gold_coin_rewards']" placeholder="请输入金币奖励"
							  v-if="(form['game_achievements_id'] && $check_field('set','gold_coin_rewards')) || (!form['game_achievements_id'] && $check_field('add','gold_coin_rewards'))" :disabled="disabledObj['gold_coin_rewards_isDisabled']"></el-input>
					<div v-else-if="$check_field('get','gold_coin_rewards')">{{form['gold_coin_rewards']}}</div>
											</el-form-item>
			</el-col>
						
	
	
		
		
						<el-col :xs="24" :sm="12" :lg="8" class="el_form_item_warp" v-if="!form['source_user_id'] && 0 > 0">
			  <el-form-item label="发放限次">
				<el-input id="limit_times" v-model="form['achievements_limit_times']" placeholder="发放限制次数，0为不限"
						  v-if="$check_option('/game_achievements/view','can_limits')"
				></el-input>
				<div v-else-if="$check_action('/game_achievements/view','get')" v-html="form['achievements_limit_times']"></div>
			  </el-form-item>
			</el-col>
			
	
	
		
		
	
		</el-row>
			<el-col :xs="24" :sm="12" :lg="8" class="el_form_btn_warp">
				<el-form-item v-if="$check_action('/game_achievements/view','set') || $check_action('/game_achievements/view','add')">
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
				field: "game_achievements_id",
				url_add: "~/api/game_achievements/add?",
				url_set: "~/api/game_achievements/set?",
				url_get_obj: "~/api/game_achievements/get_obj?",
				url_upload: "~/api/game_achievements/upload?",

				query: {
					"game_achievements_id": 0,
				},

				form: {
								"achievement_name":  '', // 成就名称
										"type_of_achievement":  '', // 成就类型
										"score_settings":  0, // 分数设置
										"gold_coin_rewards":  '', // 金币奖励
											"game_achievements_id": 0, // ID
													"achievements_limit_times": 0, // 发放限制次数
												},
				disabledObj:{
								"achievement_name_isDisabled": false,
										"type_of_achievement_isDisabled": false,
					          			"score_settings_isDisabled": false,
										"gold_coin_rewards_isDisabled": false,
										},

	
									// 成就类型选项列表
				list_type_of_achievement: ['累计','单局','彩蛋'],
	
			
			
		
			}
		},
		methods: {

	
	
			
	
			
	
			
	
		
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
					bl = this.$check_action('/game_achievements/table','add');
					console.log(bl ? "你有表格添加权限视作有添加权限" : "你没有表格添加权限");
				}
				if(!bl){
					bl = this.$check_action('/game_achievements/table','set');
					console.log(bl ? "你有表格添加权限视作有修改权限" : "你没有表格修改权限");
				}
				if(!bl){
					bl = this.$check_action('/game_achievements/view','add');
					console.log(bl ? "你有视图添加权限视作有添加权限" : "你没有视图添加权限");
				}
				if(!bl){
					bl = this.$check_action('/game_achievements/view','set');
					console.log(bl ? "你有视图修改权限视作有修改权限" : "你没有视图修改权限");
				}
				if(!bl){
					bl = this.$check_action('/game_achievements/view','get');
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
