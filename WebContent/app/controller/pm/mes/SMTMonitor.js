Ext.QuickTips.init();
Ext.define('erp.controller.pm.mes.SMTMonitor', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'pm.mes.SMTMonitor','core.form.Panel','common.query.GridPanel',
    		'core.toolbar.Toolbar','core.button.Close',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'#settingBtn' : {
    		    click:function(){
    		    	var win =  Ext.getCmp("win");
    		    	if(!win){
    		    		me.createWin();
    		    	}
    		    }
    		},
    		'erpQueryGridPanel':{
    		   	afterrender:function(grid){
    		   		grid.setVisible(false);
    		   		var win =  Ext.getCmp("win");
    		    	if(!win){
    		    		me.createWin();
    		    	}    	
    		    } 
    		}
    	});
    },
    cycling : function(){
    	var me = this;
    	var de_code = Ext.getCmp("msl_devcode").value;	
    	setTimeout(function() {
              me.cycling();
        }, me.cycleTime*1000);
        if( !Ext.isEmpty(de_code)){
	    	var querygrid = Ext.getCmp('querygrid'),de_code = Ext.getCmp("msl_devcode").value;	    	
			var gridParam = {caller: caller, condition:  ("msl_devcode='" + de_code+"' and msl_status=0 and msl_baseqty>0 order by msl_remainqty,msl_baseqty"), start: 1, end: getUrlParam('_end')||1000};
			querygrid.GridUtil.loadNewStore(querygrid, gridParam);
			//加载主表的mc_madeqty完工数字段
			Ext.Ajax.request({
				  url: basePath + 'pm/bom/getDescription.action',
				  params: {
	                tablename:'makeCraft left join makesmtlocation on msl_mcid=mc_id left join device on msl_devcode=de_code',
	                field:'mc_madeqty',
	                condition:"msl_devcode='"+de_code+"' and msl_status=0"
				  },
				  callback: function(opt, s, r) {
					var rs = Ext.decode(r.responseText);
					if(rs.exceptionInfo) {					
						showError(rs.exceptionInfo);return;
					}else if(rs.success){
						Ext.getCmp("mc_madeqty").setValue(rs.description);							
					}
				  }
			 });			  
    	}
    },
    createWin: function(){
    	var me = this;
    	var win = new Ext.window.Window({  
    		  modal : true,
        	  id : 'win',
        	  height : '35%',
        	  width : '30%',       	 
        	  layout : 'anchor',   
        	  bodyStyle: 'background: #f1f1f1;',
			  bodyPadding:5,			  
        	  items : [{
        	  	anchor: '100% 100%',
                xtype: 'form',
                bodyStyle: 'background: #f1f1f1;',
                defaults:{
        	  	  fieldStyle : "background:rgb(224, 224, 255);",    
				  labelStyle:"color:red;"
        	    },
	            items:[{
	        		  xtype:'dbfindtrigger',
	        		  name:'de_code',
	        		  fieldLabel:'机台编号',
	        		  id:'de_code',
	        		  allowBlank:false       		 
	        	  },{
	        	      xtype:'textfield',
	        		  name:'cycleTime',
	        		  fieldLabel:'刷新周期(秒)',
	        		  id:'cycleTime',
	        		  allowBlank:false
	        	  },{
	        	      xtype:'textfield',
	        		  name:'unitTime',
	        		  fieldLabel:'单件耗时(秒)',
	        		  id:'unitTime',
	        		  allowBlank:false
	        	  },{
	        	      xtype:'textfield',
	        		  name:'warningTime',
	        		  fieldLabel:'缺料预警提前(分)',
	        		  id:'warningTime',
	        		  allowBlank:false
	        	  }],
                buttonAlign : 'center',
	            buttons: [{
					text: '确定'	,
					cls: 'x-btn-gray',
					iconCls: 'x-button-icon-save',
					id:'confirmBtn',
					formBind: true, //only enabled once the form is valid
                    handler: function(btn) {                   	                  	
    					me.getFormStore();		                                             
					  }
				  }]
    	       }]
    		});
    	win.show(); 
    },
    getFormStore : function(){
    	//判断机台编号,输入的周期等是否为数字
    	var me = this;
    	var de_code = Ext.getCmp("de_code").value;
    	me.cycleTime = Ext.getCmp("cycleTime").value;
    	var querygrid = Ext.getCmp("querygrid");   	
    	querygrid.unitTime =  Ext.getCmp("unitTime").value;
    	querygrid.warningTime = Ext.getCmp("warningTime").value;
    	if(!Ext.isNumeric(me.cycleTime) || me.cycleTime <='0'){
    		showError("刷新周期必须为数值且大于0!");
    		return ;
    	}
    	if(!Ext.isNumeric(querygrid.unitTime) || querygrid.unitTime <='0'){
    		showError("单件耗时必须为数值且大于0!");
    		return ;
    	}
    	if(!Ext.isNumeric(querygrid.warningTime)|| querygrid.warningTime <='0'){
    		showError("缺料预警提前时间必须为数值且大于0!");
    		return ;
    	}
    	Ext.Ajax.request({
			url: basePath + 'pm/mes/getSMTMonitorStore.action',
			params: {de_code:de_code},
			callback: function(opt, s, r) {
				Ext.getCmp('win').setLoading(false);
				var rs = Ext.decode(r.responseText);
				var querygrid = Ext.getCmp('querygrid');
				if(rs.exceptionInfo) {
					querygrid.setVisible(false);
					showError(rs.exceptionInfo);return
				} else {
					Ext.getCmp("form").getForm().setValues(rs.message);				
					var gridParam = {caller: caller, condition:  (("msl_devcode='" + de_code+"' and msl_status=0 and msl_baseqty>0 order by msl_remainqty,msl_baseqty")), start: 1, end: getUrlParam('_end')||1000};
		            querygrid.GridUtil.loadNewStore(querygrid, gridParam);
		            querygrid.setVisible(true);
					Ext.getCmp('win').close();
					me.cycling();
				}
			}
		});
    }
});