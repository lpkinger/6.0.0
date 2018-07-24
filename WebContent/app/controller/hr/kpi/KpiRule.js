Ext.QuickTips.init();
Ext.define('erp.controller.hr.kpi.KpiRule', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	views : ['hr.kpi.KpiRule', 'core.form.Panel','core.grid.Panel2','core.form.FileField','core.form.SQLPanel','core.form.ScopeField','core.form.MultiField','core.form.YnField','core.form.CheckBoxGroup',
	          'core.button.Save','core.button.Add', 'core.button.Upload', 'core.button.Close','core.button.Delete', 'core.button.Update',
	          'core.trigger.MultiDbfindTrigger', 'common.datalist.Toolbar', 'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.AutoCodeTrigger','core.trigger.AddDbfindTrigger' ],
	         init : function() {
	        	 var me = this;
	        	 this.control({
	        		 'erpSaveButton' : {
	        			 click : function(btn) {
	        				 var form = me.getForm(btn);
	        				 if (Ext.getCmp(form.codeField).value == null
	        						 || Ext.getCmp(form.codeField).value == '') {
	        					 me.BaseUtil.getRandomNumber();// 自动添加编号
	        				 }
	        				 // 保存之前的一些前台的逻辑判定
	        				 var bool=this.check();
							if(bool==true){
								this.FormUtil.beforeSave(this);
							}
	        			 }
	        		 },
	        		 'erpDeleteButton' : {
	        			 click : function(btn) {
	        				 me.FormUtil
	        				 .onDelete(Ext.getCmp('kr_id').value);
	        			 }
	        		 },
	        		 'erpUpdateButton' : {
	        			 click : function(btn) {
	        				 var bool=this.check();
							if(bool==true){
								this.FormUtil.onUpdate(this);
							}
	        			 }
	        		 },
	        		 'erpAddButton' : {
	        			 click : function() {
	        				 me.FormUtil.onAdd('addKpiRule', '新增考核规则定义',
	        				 'jsps/hr/kpi/kpiRule.jsp');
	        			 }
	        		 },
	        		 'erpCloseButton' : {
	        			 click : function(btn) {
	        				 me.FormUtil.beforeClose(me);
	        			 }
	        		 },
	        		 'combo[name=kr_standard]' : {
	        			 afterrender : function(c) {
	        				 if (c.getValue() == 0) {
	        					 Ext.getCmp('kr_standardscore').hide();
	        				 } else {
	        					 Ext.getCmp('kr_standardscore').show();
	        				 }
	        			 },
	        			 select : function(c) {
	        				 if (c.value==0) {
	        					 Ext.getCmp('kr_standardscore').hide();
	        				 } else {
	        					 Ext.getCmp('kr_standardscore').show();
	        				 }
	        			 }
	        		 },
	        		 'combo[name=kr_datafrom]' : {
	        			 afterrender : function(c) {
	        				 Ext.getCmp('kr_query').hide();
	        				 if (c.getValue() == 'SQL'||c.getValue() == 'PROCEDURE') {
	        					 Ext.getCmp('kr_query').show();
	        				 }
	        			 },
	        			 select : function(c) {
	        				 var type = c.getValue();
	        				 if (type ==  'NO') {
	        					 Ext.getCmp('kr_query').hide();
	        				 } else {
	        					 Ext.getCmp('kr_query').show();
	        				 }
	        			 }
	        		 },
	        		 'combo[name=kr_gradetype]' : {
	        			 afterrender : function(c) {
	        				 Ext.getCmp('kr_sql').hide();
	        				 if (c.getValue() == 'calculate') {
	        					 Ext.getCmp('kr_sql').show();
	        				 }
	        			 },
	        			 select : function(c) {
	        				 var type = c.getValue();
	        				 if (type != 'calculate') {
	        					 Ext.getCmp('kr_sql').hide();
	        				 } else {
	        					 Ext.getCmp('kr_sql').show();
	        				 }
	        			 }
	        		 },
	        		'htmleditor[name=kr_sql]' : {
	        			 beforerender:function(editor){
	        				 var showEdit=function(){
	        					 Ext.create('Ext.window.Window', {
		        					 title : '计算公式',
		        					 id:'kpisqlwin',
		        					 closeAction : 'destroy',
		        					 items : [{
		        						 xtype : 'erpSQLPanel',
		        						 anchor : '100% 100%'
		        					 }]
		        				 }).show();
	        				 };
	        				 Ext.apply(editor,{
	        					 columnWidth:1,
	        					 readOnly:true,
	        					 createToolbar:function(editor){
	        						   editor.toolbar = Ext.widget('toolbar', {
	        					            renderTo: editor.toolbarWrap,
	        					            enableOverflow:false,	        					        
	        					            ui:'footer',
	        					            items: [{
	        					            	xtype:'button',	 	        					            	
	        					            	iconCls:'x-button-icon-edit',
	        					            	text:'编辑',
	        					            	handler:showEdit
	        					            }]
	        					        });
	        					 },
	        					 disableItems:function(){
	        						 
	        					 }
	        				 });

	        			 }
	        		 }
	        	 });
	         },
	         getForm : function(btn) {
	        	 return btn.ownerCt.ownerCt;
	         },
	         check : function(){
	        	 	var score1=Ext.getCmp('kr_score_from').value-0;//最低分
					var score2=Ext.getCmp('kr_score_to').value-0;//最高分
					var score3=Ext.getCmp('kr_standardscore').value-0;//标准分
					var kr_datafrom=Ext.getCmp('kr_datafrom').value;
					var kr_gradetype=Ext.getCmp('kr_gradetype').value;
					var bool=true;
					if(score3>score2 || score3<score1){
						showError("标准分应在评分范围内");		
						return false;
					}
					if(kr_datafrom== 'SQL'||kr_datafrom== 'PROCEDURE'){
						if(Ext.getCmp('kr_query').value==''){
							showError("表达式不能为空");		
							return false;
						}
					}
					if(kr_gradetype== 'calculate'){
						if(Ext.getCmp('kr_sql1').value==''){
							showError("计算公式不能为空");	
							return false;
							//bool=false;
						}
					}
					return true;;
	         }
});