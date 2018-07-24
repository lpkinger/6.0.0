Ext.define('erp.view.plm.project.ProjectEvaluation',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'plm/project/saveProjectEvaluation.action',
					deleteUrl: 'plm/project/deleteProjectEvaluation.action',
					updateUrl: 'plm/project/updateProjectEvaluation.action',
					auditUrl: 'plm/project/auditProjectEvaluation.action',
					resAuditUrl: 'plm/project/resAuditProjectEvaluation.action',
					submitUrl: 'plm/project/submitProjectEvaluation.action',
					resSubmitUrl: 'plm/project/resSubmitProjectEvaluation.action',
					getIdUrl: 'common/getId.action?seq=ProjectEvaluation_SEQ',
					keyField: 'pe_id',
					statuscodeField: 'pe_statuscode',
					getItemsAndButtons: function(form, url, param){
						var datalistId = getUrlParam('datalistId');
						var type=getUrlParam('pe_type');
						if(datalistId||type||param.condition){
							if(type){
							   param.caller='ProjectEvaluation'+type;
							   caller=param.caller;
							   param.condition ='pe_id='+getUrlParam('pe_id');
							}
							form.FormUtil.getItemsAndButtons(this, 'common/singleFormItems.action', this.params || param);
						}else{
								Ext.create('Ext.window.Window', {
	    						autoShow: true,
	    						width: 300,
	    						height: 200,
	    						id:'projectWin',
	    						layout: 'anchor',
	    						items: [{ 							    					
	  							  anchor:'100% 100%',
	  							  xtype:'form',
	  							  buttonAlign : 'center',
	  							  items:[{
	  							        xtype: 'combo',
	  							        id:'type',
	  							        width:280,
	  									fieldLabel: '项目类别', 									
	  									store: Ext.create('Ext.data.Store', {
	  										autoLoad: true,
	  									    fields: ["display", "value"],
	  									    data:[{value: "STANDARD", display: "标准方案"}, {value: "CUSTOM", display: "定制方案"}] 									 
	  									}),
	  									queryMode: 'local',
	  								    displayField: 'display',
	  								    valueField: 'value',
	  								    allowBlank:false,
	  								    selectOnFocus:true,
	  								    editable:'false',
	  								    value:'STANDARD'
	  								},{
	  									xtype:'dbfindtrigger',
	  									width:280,
	  									id:'templet',
	  									fieldLabel:'方案模板编号',
	  									name: "templet"
	  								}]	 							    	     				    							           	
	  						 }], 
	    						buttonAlign: 'center',
	    						buttons: [{
	    							text: '确定',
	    							handler: function(b) {
	    								var type=Ext.getCmp('type').value;
	    								var templet=Ext.getCmp('templet').value;
	    								if(!type){
	    									showError('请选择项目类别');
	    									return;
	    								}else if(type=='STANDARD'&&!templet){
	    									showError('请选择方案模板');
	    									return;
	    								}
	    								if(templet){
		    								if(type=='STANDARD'){
		    									this.caller='ProjectEvaluationSTANDARD';
		    								}else{
		    									this.caller='ProjectEvaluationCUSTOM';
		    								}
											var param = {caller: this.caller || caller, condition:"pe_tempcode='"+templet+"' and pe_type='TEMPLET'", _noc: (getUrlParam('_noc') || this._noc)};
											me.setLoading(true);
											Ext.Ajax.request({//拿到form的items
												url : basePath + url,
												params: param,
												method : 'post',
												callback : function(options, success, response){
													me.setLoading(false);
													Ext.getCmp('projectWin').close();
													if (!response) return;
													var res = new Ext.decode(response.responseText);
													if(res.exceptionInfo != null){
														showError(res.exceptionInfo);return;
													}
													form.fo_id = res.fo_id;
													form.fo_keyField = res.keyField;
													form.tablename = res.tablename;//表名
													if(res.keyField){//主键
														form.keyField = res.keyField;
													}
													if(res.statusField){//状态
														form.statusField = res.statusField;
													}
													if(res.statuscodeField){//状态码
														form.statuscodeField = res.statuscodeField;
													}
													if(res.codeField){//Code
														form.codeField = res.codeField;
													}
													form.fo_detailMainKeyField = res.fo_detailMainKeyField;//从表外键字段
													var grids = Ext.ComponentQuery.query('gridpanel');
													//如果该页面只有一个form，而且form字段少于8个，则布局改变
													if(!form.fixedlayout && !form.minMode && grids.length == 0 && res.items.length <= 8){
														Ext.each(res.items, function(item){
															item.columnWidth = 0.5;
														});
														form.layout = 'column';
													}
													if((res.items.length>0.097*window.innerWidth && window.innerWidth<=1150)){
														Ext.each(res.items, function(item){
															form.layout='column';
															//若根据分辨率直接获取宽度会导致 有时不能占满整行
															item.width=window.innerWidth*(item.columnWidth)-item.columnWidth*4*10;
														});
													}
													//data&items
													var items = form.setItems(form, res.items, res.data, res.limits,type, {
														labelColor: res.necessaryFieldColor
													});
													form.add(items);
													//title
													if(res.title && res.title != ''){
														form.setTitle(res.title);
														var _tt = res.title;
														if(form.codeField) {
															var _c = form.down('#' + form.codeField);
															if( _c && !Ext.isEmpty(_c.value) )
																_tt += '(' + _c.value + ')';
														}
													}
													if(!form._nobutton) form.FormUtil.setButtons(form,'erpSaveButton#erpCloseButton');
													form.fireEvent('afterload', form);
												}
											});
	    								}else{
								  			 var param = {caller:'ProjectEvaluationCUSTOM', condition:'', _noc: (getUrlParam('_noc') || this._noc)};
											 form.FormUtil.getItemsAndButtons(form, 'common/singleFormItems.action', this.params || param);
											 Ext.getCmp('projectWin').close();
										}
	    							}
	    						}, {
	    							text: '取消',
	    							handler: function(b) {
	    								b.ownerCt.ownerCt.close();
	    							}
	    						}]
	    					});  
						}
						},
						setItems: function(form, items, data, limits, type,necessaryCss){
						var me=this,edit = !form.readOnly,hasData = true,limitArr = new Array();
						if(limits != null && limits.length > 0) {//权限外字段
							limitArr = Ext.Array.pluck(limits, 'lf_field');
						}
						if (data) {
							data = Ext.decode(data);
							data.pe_id='';
							data.pe_statuscode='ENTERING';
							data.pe_status='在录入';
							data.pe_type=type;
							data.pe_recorderid=em_id;
							data.pe_recordername=em_name;
							var oDate = new Date(); //实例一个时间对象；
							data.pe_recorddate=oDate;
							data.pe_auditer='';
							data.pe_auditdate='';
						} else {
							hasData = false;
						}
						var bool = 'a';
						if(items.length > 110&&items.length <=190){
							bool = 'b';
						}else if(items.length>190){
							bool = 'c';
						}
						Ext.each(items, function(item){
							if(screen.width < 1280){//根据屏幕宽度，调整列显示宽度
								if(item.columnWidth > 0 && item.columnWidth <= 0.25){
									item.columnWidth = 1/3;
								} else if(item.columnWidth > 0.25 && item.columnWidth <= 0.5){
									item.columnWidth = 2/3;
								} else if(item.columnWidth >= 1){
									item.columnWidth = 1;
								}
							} else {
								if(item.columnWidth > 0.25 && item.columnWidth < 0.5){
									item.columnWidth = 1/3;
								} else if(item.columnWidth > 0.5 && item.columnWidth < 0.75){
									item.columnWidth = 2/3;
								}
							}
							if(!item.allowBlank && item.fieldLabel && necessaryCss.labelColor) {
								item.labelStyle = 'color:#' + necessaryCss.labelColor;
								item.fieldStyle = 'background:#FFFAFA;color:#515151;';
							}
							if(item.readOnly) {
								item.fieldStyle = 'background:#e0e0e0;';
							}
							
							if(item.renderfn){
								if(contains(item.renderfn, ':', true)){
					    			var args = new Array();
					    			Ext.each(item.renderfn.split(':'), function(a, index){
					    				if(index == 0){
					    					renderName = a;
					    				} else {
					    					args.push(a);
					    				}
					    			});
					    		}
								me[renderName](item,args);
							}
							if(item.name != null) {
								if(item.name == form.statusField){//状态加特殊颜色
									item.fieldStyle = item.fieldStyle + ';font-weight:bold;';
								} else if(item.name == form.statuscodeField){//状态码字段强制隐藏
									item.xtype = 'hidden';
								}
							}
							if(item.xtype == 'hidden') {
								item.columnWidth = 0;
								item.margin = '0';
							}
							if(item.xtype == 'checkbox') {
								item.focusCls = '';
							}
							if(item.maskRe!=null){
								item.maskRe=new RegExp(item.regex);
							}
							if (hasData) {
								item.value = data[item.name];
								if(item.secondname){//针对合并型的字段MultiField
									item.secondvalue = data[item.secondname];
								}
								if(!edit){
									form.readOnly = true;
									item.fieldStyle = item.fieldStyle + ';background:#f1f1f1;';
									item.readOnly = true;
								} 
								if(item.xtype == 'checkbox'){
									item.checked = Math.abs(item.value || 0) == 1;
									item.fieldStyle = '';
								}
							}
							form.FormUtil.setItemWidth(form, items);
							if(limitArr.length > 0 && Ext.Array.contains(limitArr, item.name)) {
								item.hidden = true;
							}
				
							if(item.html&&item.name == null&&item.value==''){
				
							}else{
								if(bool == 'b') {
									item.columnWidth = item.columnWidth*0.83;
								}
								if(bool == 'c') {
									item.columnWidth = item.columnWidth*0.85;
								}
							}
				
						});
						return items;
					}
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});