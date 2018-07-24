Ext.QuickTips.init();
Ext.define('erp.controller.crm.chance.BusinessChanceData', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'crm.chance.BusinessChanceData','core.form.Panel','core.form.FileField','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger',
  			'core.form.CheckBoxGroup',
  	],
	init:function(){
		var me = this;
		this.control({
			'erpFormPanel':{
				afterload:function(form){			
					var item = [ 
					{
						allowBlank: false,
						allowDecimals: true,
						border: false,
						bcd_columnWidth:1,  
						html: "<div onclick='javascript:collapse(0);' class='x-form-group-label' id='group0' style='background-color: #E8E8E8;height:22px;width:80%;!important;' title='收拢'><h6>阶段要点</h6></div>",
						xtype: "container",
						columnWidth:1
					}
					];
					form.add(item);
					me.showPoints(form);	
				}
			},
    		'dbfindtrigger': {
    			change: function(trigger){
					var form = trigger.ownerCt;
	 				if(trigger.name == 'bcd_bscode'){
    					me.showPoints(form);
    				}
    			},
    		},
    		'dbfindtrigger[name=bc_code]':{
    			afterrender:function(t){
					t.autoShowTriggerWin=false;
    			}
    		},
			'erpSaveButton': {

				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					//保存之前的一些前台的逻辑判定
					this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('bcd_id').value);
				}
			},
			'erpUpdateButton': {			
				click: function(btn){
					var form = me.getForm(btn);
					this.FormUtil.onUpdate(this);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('bcd_statuscode');
					if(statu && statu.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('bcd_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('bcd_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('bcd_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('bcd_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('bcd_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('bcd_statuscode');
					if(statu && statu.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('bcd_id').value);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addBusinessChanceData', '新增商机动态', 'jsps/crm/chance/BusinessChanceData.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'#bcd_sourcecode': {
	  			   afterrender:function(f){
	 				   f.setFieldStyle({
	 					   'color': 'blue'
	 				   });
	 				   f.focusCls = 'mail-attach';
	 				   var c = Ext.Function.bind(me.openSource, me);
	 				   Ext.EventManager.on(f.inputEl, {
	 					   mousedown : c,
	 					   scope: f,
	 					   buffer : 100
	 				   });

	  			   }
			}
    	});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	openSource: function(e, el, obj) {
		var f = obj.scope, form = f.ownerCt,
			i = form.down('#bcd_sourcelink');
		if(i && i.value) {
			openUrl(i.value);
		}
	},
	showPoints:function(form){	
		var bscode = form.getComponent('bcd_bscode').value;
		
		var formitems = form.items.items;

		var delnum = formitems.length;
		
		var bcdId = form.getComponent("bcd_id").value;
		var params = {};
		params.bs_code = bscode;
		params.bcd_id = bcdId;
		var data = JSON.stringify(params);
		
		//ajax加载数据
		Ext.Ajax.request({
			url : basePath + "crm/chance/getpointsanddata.action",
			method : 'post',
			params:{
				parameters:data
			},
			callback : function(options,success,response){

				//先将原来的阶段要点item删除
				for(var i=delnum;i>=0;i--){
					if(formitems[i]){
						if(typeof(formitems[i].id)!="undefined"){
      						var itemname = formitems[i].id;
    						if(itemname.indexOf("bcd_column")>-1){
								form.remove(formitems[i]);
							}
						}
					}
				};
				
	   			var data = Ext.decode(response.responseText);
	
	   			var pointValueDesc = "";
	   			var pointValueFlag = "";
	   			var pointValueDetno = "";
	   			
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
				
				var writeFlag = false;
				
				for(var i=0;i<detnoArr.length;i++){
						var labelStyle;
						//根据point和data动态加载items					
						if(flagArr[i]=="1"){
							writeFlag = false;
							labelStyle = "color:#FF0000";
						}else if(flagArr[i]=="0"){
							writeFlag = true;
							labelStyle = null;
						}
						
						var value = null;
						if(data["columndata"]!=null){
							value = data.columndata["BCD_COLUMN"+detnoArr[i]];
						}
						
						var item = [
						            {
						      fieldLabel: descArr[i], 
						      name: "bcd_column"+detnoArr[i], 
						      id: "bcd_column"+detnoArr[i], 
						      group: 1, 
						      table: "BusinessChanceStage", 
						      xtype: "textareatrigger", 
						      readOnly: false, 
						      dataIndex: "bcd_column"+detnoArr[i], 
						      maxLength: 100, 
						      maxLengthText: "字段长度不能超过100字符!", 
						      hideTrigger: false, 
						      editable: true, 
						      columnWidth: 0.25, 
						      value: value, 
						      allowBlank: writeFlag, 
						      cls: "form-field-allowBlank", 
						      fieldStyle: "background:#ffffff;color:#515151;", 
						      labelAlign: "left",  
						      labelStyle:labelStyle
						    },
						    ];
						
						form.add(item);
				}
				
			}
			});

	}
});