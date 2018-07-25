Ext.QuickTips.init();
Ext.define('erp.controller.pm.mes.OverStationGet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'pm.mes.OverStationGet','core.trigger.DbfindTrigger',    		
    		'core.button.Close'
    	],
    init:function(){
    	var me = this;    	
    	this.control({  
    		'#form':{
    			beforerender:function(){
    				me.createWin();
    			}
    		},
    		'#sn_code': { 
    		    specialkey: function(f, e){//按ENTER执行确认
    				if (e.getKey() == e.ENTER) {//序列号采集
    					if(f.value != null && f.value != '' ){
    						me.confirmSnCodeGet(f.value);
        				}
    				}
    			}
    		} 
    	});   	
    },
    createWin :function(){
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
	        		  name:'scCode',
	        		  fieldLabel:'资源编号',
	        		  id:'scCode',
	        		  allowBlank:false       		 
	        	  },{
	        	      xtype:'dbfindtrigger',
	        		  name:'mcCode',
	        		  fieldLabel:'作业单号',
	        		  id:'mcCode',
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
                        me.getFormStore({scCode:Ext.getCmp("scCode").value,mcCode:Ext.getCmp("mcCode").value});	                        
					  }
				  }]
    	       }]
    		});
    	win.show(); 
    },
   getFormStore : function(a){
   	  Ext.Ajax.request({
	    	 url : basePath + 'pm/mes/getOverStationStore.action',
	    	 params: a,
	    	 method : 'post',
	    	 callback : function(opt, s, res) {
	    	    var r = Ext.decode(res.responseText);
	    	     if (r.exceptionInfo) {                   
                    showError(r.exceptionInfo);
                    return;
                  }
	    	    if (r.datas) {	    	    	
                    Ext.getCmp("form").getForm().setValues(r.datas);
                    Ext.getCmp("win").close();
	    	    }
	    	 }
	    });
   },
   confirmSnCodeGet:function(f){//确认过站采集
     var sn_code = Ext.getCmp("sn_code").value, mc_code = Ext.getCmp("mc_code").value,
     sc_code = Ext.getCmp("sc_code").value,st_code = Ext.getCmp("st_code").value,
     result = Ext.getCmp("t_result"),combineChecked = Ext.getCmp('getCombine').value;
     if(Ext.isEmpty(sc_code)){
     	showError("请先选择资源编号!");
     	return;
     }
     if(Ext.isEmpty(sn_code)){
     	showError("请录入资源编号!");
     	return;
     }
   	  Ext.Ajax.request({
	    	 url : basePath + 'pm/mes/confirmSnCodeGet.action',
	    	 params: {sc_code:sc_code, mc_code:mc_code,sn_code:sn_code,st_code:st_code,combineChecked:combineChecked},
	    	 method : 'post',
	    	 callback : function(opt, s, res) {
	    	    var r = Ext.decode(res.responseText);
	    	     if (r.exceptionInfo) {
                    result.append(r.exceptionInfo, 'error');
                    showError(res.exceptionInfo);
                    Ext.getCmp("sn_code").setValue('');
                    return;
                  }
	    	     if (r.success) {
	    	     	if(combineChecked){
	    	     		 result.append('过站采集,拼板采集,序列号：' + sn_code + '成功！');
	    	     	}else {
	    	     	   result.append('过站采集,序列号：' + sn_code + '成功！');
	    	     	}
	    	    	Ext.getCmp("form").getForm().setValues(r.datas);
	    	    	Ext.getCmp("sn_code").setValue('');
	    	     }	    	    
	    	 }
	    });
   }
});