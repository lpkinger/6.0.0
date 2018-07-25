Ext.QuickTips.init();
Ext.define('erp.controller.crm.chance.BusinessChanceStage', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'crm.chance.BusinessChanceStage','core.form.Panel','core.form.FileField','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
  			'core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar','core.form.ColorField',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger',
  			'core.button.AddPoint'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpFormPanel':{
				afterload:function(form){
					//加载阶段要点
					var item = [ 
								{
									allowBlank: false,
									allowDecimals: true,
									border: false,
									columnWidth:1,  
									html: "<div onclick='javascript:collapse(0);' class='x-form-group-label' id='group0' style='background-color: #E8E8E8;height:22px;width:80%;!important;' title='收拢'><h6>阶段要点</h6></div>",
									xtype: "container",
								}
								];
					form.add(item);
					
					me.FormUtil.getActiveTab().setLoading(true);
					
					var params = {};
					params.bs_code = Ext.getCmp("bs_code").value;
					var datatrans = JSON.stringify(params);
					
					Ext.Ajax.request({
				   		url : basePath + "crm/chance/getpoint.action",
				   		method : "post",
						params:{
							parameters:datatrans
						},
				   		callback : function(options,success,response){
				   			me.FormUtil.getActiveTab().setLoading(false);
				   			
				   			var data = Ext.decode(response.responseText);

				   			var pointValueDesc = "";
				   			var pointValueFlag = "";
				   			var pointValueDetno = "";
				   			
				   			var bsPoint = data["bs_point"];
				   			
				   			if(data["bs_point"]!=null){
				   				pointValueDesc = data["bs_point"];
				   			}
				   			if(data["bs_pointflag"]!=null){
				   				pointValueFlag = data["bs_pointflag"];
				   			}
				   			if(data["bs_pointdetno"]!=null){
				   				pointValueDetno = data["bs_pointdetno"]; 			
				   			}
							
							var descArr = null;
							if(pointValueDesc!=null){
								descArr = pointValueDesc.split('#');
							}
							var flagArr = null;
							if(pointValueFlag!=null){
								flagArr = pointValueFlag.split('#');
							}
							var detnoArr = [];

							if(pointValueDetno!=null&&pointValueDetno!=""){
								detnoArr = pointValueDetno.split('#');							
							}
							
							if(descArr!=null&&flagArr!=null){
								var writeFlag = false;
								
								//加载阶段要点
								var name = form.getComponent('bs_name');
								var nameValue = name.value;
								
								var bsIdValue = form.getComponent('bs_id').value;
								
								var stageDetno = form.getComponent('bs_detno').value;
															
								var yesFlag = false;
								var noFlag;							
								
								for(var i=0;i<detnoArr.length;i++){
										var desc = descArr[i];
										var fl = flagArr[i];
										
										//yes为1，no为0
										if(fl=='1'){
											yesFlag = true;
										}else if(fl='0'){
											yesFlag = false;
										}
										
										noFlag = !yesFlag;
										
										var itemTri = [{
											allowBlank: false,
											allowDecimals: true,
											border: false,
											columnWidth:1,  
											margin:'5 0 0 0',
											xtype: "container",
											layout:'column',
											name:'pointContainer',
											items:[{
												xtype : 'triggerfield',
												minValue : 0,
												fieldLabel : '描述',
												fieldStyle:'background:#fff;',
												labelAlign: 'right',
												
												stage:stageDetno,  //商机阶段顺序
												detno:detnoArr[i], //阶段要点顺序
												
												columnWidth : .25,
												cls: "form-field-allowBlank",
												triggerCls: Ext.baseCSSPrefix + 'form-clear-trigger',
												tooltip:'删除要点',
												onTriggerClick:function(){
													form.remove(this.ownerCt);
												},
												value:desc
											},
											 {
											        xtype: 'checkboxgroup',
											        fieldLabel: '必填',
											        layout:'column',
											        // Arrange radio buttons into two columns, distributed vertically
											        columns: 2,
											        vertical: true,
											        items: [
											            {  margin:'5 0 0 0',inputValue: 'yes' ,columnWidth:1,checked:yesFlag}
											        ]
											    }]
										}];
										if(bsIdValue){
											form.add(itemTri);
									}											
								}
							}
				   		}
					});
				}
			},
			'erpAddPointButton':{
				click:function(btn){
					var form = me.getForm(btn);
					
					var bsDetno = form.getComponent('bs_detno').value;
					if(!bsDetno){
						showError("请先选择商机阶段顺序");
						return;
					}
					

					var item = [ 
								{
									margin:'5 0 0 0',
									allowBlank: false,
									allowDecimals: true,
									border: false,
									columnWidth:1,  
									xtype: "container",
									layout:'column',
									name:'pointContainer',
									items:[{
										xtype : 'triggerfield',
										stage:bsDetno,  //商机阶段顺序
										detno:-1, //阶段要点顺序
										
										minValue : 0,
										fieldLabel : '描述',
										labelAlign: 'right',
										fieldStyle:'background:#fff;',
										columnWidth : .25,
										cls: "form-field-allowBlank",
										triggerCls: Ext.baseCSSPrefix + 'form-clear-trigger',
										tooltip:'删除要点',
										onTriggerClick:function(){
											form.remove(this.ownerCt);
										}
									}
									 ,
									 {
									        xtype: 'checkboxgroup',
									        fieldLabel: '必填',
									        layout:'column',
									        columns: 2,
									        vertical: true,
									        items: [
									            {  margin:'5 0 0 0',inputValue: 'yes' ,columnWidth:1},
									        ]
									    },

									 ]
								}
								];				

					form.add(item);					
					
			}
			},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					
					me.setPoints(form);

					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}						
					this.FormUtil.beforeSave(this);				
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('bs_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					var form = me.getForm(btn);

					me.setPoints(form);
						
					this.FormUtil.onUpdate(this);
					
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('bs_statuscode');
					if(statu && statu.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('bs_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('bs_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('bs_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('bs_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('bs_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('bs_statuscode');
					if(statu && statu.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('bs_id').value);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addBusinessChanceStage', '新增商机阶段', 'jsps/crm/chance/BusinessChanceStage.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'field[name=bs_name]' : {
				beforerender : function(f) {
					if (f.value != null && f.value != ''){
						f.editable=false;	
						f.readOnly=true;
					}
				}
			},
			'field[name=bs_detno]' : {
				beforerender : function(f) {
					if (f.value != null && f.value != ''){
						f.editable=false;
						f.readOnly=true;
					}
				}
			}
    	});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	setPoints:function(form){
		var pointValueDescTemp = "";
		var pointValueFlagTemp = "";
		var pointValueDetnoTemp = "";
		
		var pointValue = "";
		form.items.items.forEach(function(it){
			if(it.name){
				if(it.name=='pointContainer'){
					var fieldItem = it.items.items[0];
					
					var triFieldVal = fieldItem.value;
					
					var test = it.items.items[1].getChecked();
					var che;
					if(typeof(triFieldVal)!="undefined"){
						if(""!=triFieldVal.replace(/\s/g, "")){
							if(test.length==0){
								che = 'no';	
								pointValueFlagTemp += "#0"	;
								
							}else{
								che = it.items.items[1].getChecked()[0].inputValue; //因为只有一个单选框，所以取第一个元素
								pointValueFlagTemp += "#1";

							}					

							pointValueDescTemp += "#" + triFieldVal;
							pointValueDetnoTemp += "#" + fieldItem.detno;		
						}			
					}				
				}
			}
		});
		var point = form.getComponent('bs_point');
		if(pointValueDescTemp.indexOf("#")==0){
			pointValueDescTemp = pointValueDescTemp.substring(1);
		}
		point.setValue(pointValueDescTemp);
		
		var pointFlag = form.getComponent("bs_pointflag");
		if(pointValueFlagTemp.indexOf("#")==0){
			pointValueFlagTemp = pointValueFlagTemp.substring(1);
		}
		pointFlag.setValue(pointValueFlagTemp);
		
		var pointDetno = form.getComponent("bs_pointdetno");
		if(pointValueDetnoTemp.indexOf("#")==0){
			pointValueDetnoTemp = pointValueDetnoTemp.substring(1);
		}		
		pointDetno.setValue(pointValueDetnoTemp);
	}
});

