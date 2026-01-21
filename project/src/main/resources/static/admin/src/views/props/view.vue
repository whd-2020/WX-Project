<template>
	<el-main class="bg edit_wrap comtable_e">
		<el-form ref="form" :model="form" status-icon label-width="120px" v-if="is_view()">
		<el-row class="row_ce"> 
							<el-col v-if="$check_field('get','prop_name') || $check_field('add','prop_name') || $check_field('set','prop_name')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="道具名称" prop="prop_name">
															<el-input id="prop_name" v-model="form['prop_name']" placeholder="请输入道具名称"
							  v-if="(form['props_id'] && $check_field('set','prop_name')) || (!form['props_id'] && $check_field('add','prop_name'))" :disabled="disabledObj['prop_name_isDisabled']"></el-input>
					<div v-else-if="$check_field('get','prop_name')">{{form['prop_name']}}</div>
											</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','props_picture') || $check_field('add','props_picture') || $check_field('set','props_picture')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="道具图片" prop="props_picture">
											<el-upload :disabled="disabledObj['props_picture_isDisabled']" class="avatar-uploader" drag
						accept="image/gif, image/jpeg, image/png, image/jpg" action="" :http-request="upload_props_picture"
						:show-file-list="false" v-if="(form['props_id'] && $check_field('set','props_picture')) || (!form['props_id'] && $check_field('add','props_picture'))">
						<img id="props_picture" v-if="form['props_picture']" :src="$fullUrl(form['props_picture'])" class="avatar">
						<i v-else class="el-icon-plus avatar-uploader-icon"></i>
					</el-upload>
					<el-image v-else-if="$check_field('get','props_picture')" style="width: 100px; height: 100px"
						:src="$fullUrl(form['props_picture'])" :preview-src-list="[$fullUrl(form['props_picture'])]">
						<div slot="error" class="image-slot">
							<img src="../../../public/img/error.png" style="width: 90px; height: 90px" />
						</div>
					</el-image>
							</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','gold_coins_required') || $check_field('add','gold_coins_required') || $check_field('set','gold_coins_required')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="所需金币" prop="gold_coins_required">
															<el-input id="gold_coins_required" v-model="form['gold_coins_required']" placeholder="请输入所需金币"
							  v-if="(form['props_id'] && $check_field('set','gold_coins_required')) || (!form['props_id'] && $check_field('add','gold_coins_required'))" :disabled="disabledObj['gold_coins_required_isDisabled']"></el-input>
					<div v-else-if="$check_field('get','gold_coins_required')">{{form['gold_coins_required']}}</div>
											</el-form-item>
			</el-col>
								<el-col v-if="$check_field('get','props_use') || $check_field('add','props_use') || $check_field('set','props_use')" :xs="24" :sm="12" :lg="8" class="el_form_item_warp">
							<el-form-item label="道具用途" prop="props_use">
											<el-select id="props_use" v-model="form['props_use']"
						v-if="(form['props_id'] && $check_field('set','props_use')) || (!form['props_id'] && $check_field('add','props_use'))">
						<el-option v-for="o in list_props_use" :key="o" :label="o" :value="o">
						</el-option>
					</el-select>
					<div v-else-if="$check_field('get','props_use')">{{form['props_use']}}</div>
							</el-form-item>
			</el-col>
						
	
	
		
		
						<el-col :xs="24" :sm="12" :lg="8" class="el_form_item_warp" v-if="!form['source_user_id'] && 0 > 0">
			  <el-form-item label="购买限次">
				<el-input id="limit_times" v-model="form['purchase_record_limit_times']" placeholder="购买限制次数，0为不限"
						  v-if="$check_option('/props/view','can_limits')"
				></el-input>
				<div v-else-if="$check_action('/props/view','get')" v-html="form['purchase_record_limit_times']"></div>
			  </el-form-item>
			</el-col>
			
	
	
		
		
	
		</el-row>
			<el-col :xs="24" :sm="12" :lg="8" class="el_form_btn_warp">
				<el-form-item v-if="$check_action('/props/view','set') || $check_action('/props/view','add')">
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
				field: "props_id",
				url_add: "~/api/props/add?",
				url_set: "~/api/props/set?",
				url_get_obj: "~/api/props/get_obj?",
				url_upload: "~/api/props/upload?",

				query: {
					"props_id": 0,
				},

				form: {
								"prop_name":  '', // 道具名称
										"props_picture":  '', // 道具图片
										"gold_coins_required":  '', // 所需金币
										"props_use":  '', // 道具用途
											"props_id": 0, // ID
													"purchase_record_limit_times": 0, // 购买限制次数
												},
				disabledObj:{
								"prop_name_isDisabled": false,
										"props_picture_isDisabled": false,
										"gold_coins_required_isDisabled": false,
										"props_use_isDisabled": false,
										},

	
			
			
									// 道具用途选项列表
				list_props_use: ['提示','打乱','增加步数'],
	
		
			}
		},
		methods: {

	
	
						/**
			 * 上传道具图片
			 * @param {Object} param 图片参数
			 */
			upload_props_picture(param){
									this.uploadFile(param.file, "props_picture");
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
					bl = this.$check_action('/props/table','add');
					console.log(bl ? "你有表格添加权限视作有添加权限" : "你没有表格添加权限");
				}
				if(!bl){
					bl = this.$check_action('/props/table','set');
					console.log(bl ? "你有表格添加权限视作有修改权限" : "你没有表格修改权限");
				}
				if(!bl){
					bl = this.$check_action('/props/view','add');
					console.log(bl ? "你有视图添加权限视作有添加权限" : "你没有视图添加权限");
				}
				if(!bl){
					bl = this.$check_action('/props/view','set');
					console.log(bl ? "你有视图修改权限视作有修改权限" : "你没有视图修改权限");
				}
				if(!bl){
					bl = this.$check_action('/props/view','get');
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
