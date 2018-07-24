/**
 * 自动获取包装箱号的trigger
 */
Ext.define('erp.view.core.trigger.BoxCodeTrigger', {
    extend: 'Ext.form.field.Trigger',
    alias: 'widget.boxcodetrigger',
    triggerCls: 'x-form-autocode-trigger',
    afterrender: function() {
        this.addEvent({
            'beforetrigger': true
        });
    },
    onTriggerClick: function() {      				   			    	
		    var mc_makecode = Ext.getCmp("mc_makecode").value,			
		    mc_prodcode = Ext.getCmp("mc_prodcode").value , pr_id = Ext.getCmp("pr_id").value,
		    sc_code = Ext.getCmp("sc_code").value,result = Ext.getCmp('t_result');
		    if(Ext.isEmpty(sc_code)){
			   showError('请先指定资源编号!');
				return ;			
			}else if(Ext.isEmpty(mc_makecode)){
				showError('请先指定制造单号!');
				return ;	
			}
			  Ext.create('Ext.window.Window', {
					title: '生成包装箱号',
					height: 200,
					width: 300,
					xtype: 'form',
					id:'win',
					buttonAlign:'center',
					bodyPadding:5,
					defaults:{
						 fieldStyle : "background:rgb(224, 224, 255);",    
				  		 labelStyle:"color:red;"
					},
					allowDrag:false,
					items: [{
						xtype:'textfield',
					    fieldLabel:'包装箱号',
					    name:'outcode',
					    id:'outcode',
					    allowBlank:false
					},{
					    xtype:'textfield',
					    fieldLabel:'箱内数量',
					    name:'num',
					    id:'num',
					    allowBlank:false
					 }], 
					 buttons:[{
						 text:$I18N.common.button.erpConfirmButton,	 
						 xtype:'button',
						 formBind: true,
						 handler:function(){
						 	var num = Ext.getCmp("num").value,code = Ext.getCmp('outcode').value;
						 	if(Ext.isEmpty(code)|| code == null || code==''){
						    	showError("箱号不允许为空!");
							    return ;
						    }
						 	if(!Ext.isNumeric(num)){
						 		showError("请输入数字类型的箱内数量");
						 		Ext.getCmp("num").setValue(0);
						 		return;
						 	}
							if(Ext.isEmpty(num) || num == 0 || num == '0'){
							    showError("箱内容量不允许为空或者零!");
							    return ;
						    }						    
							 Ext.Ajax.request({//拿到grid的columns
								url : basePath + "pm/mes/generatePackage.action",
								params: {
								   pa_totalqty   : num,  //箱内容量
								   pa_prodcode   : mc_prodcode,  //物料编号
								   pr_id         : pr_id,        //物料ID
								   pa_makecode   : mc_makecode,	 //制造单号
								   pa_outboxcode : code //包装箱号
								 },
								 method : 'post',
								 callback : function(options,success,response){
								 var res = new Ext.decode(response.responseText);
								 if(res.exceptionInfo){
								      result.append(res.exceptionInfo,'error');
								          showError(res.exceptionInfo);return;
								      }		
								       var data = res.data;
								        if(data ){//设置包装箱号					        	 
										      result.append('生成箱号：'+data['pa_code']+'成功！'); 
										      Ext.MessageBox.alert('系统提示', '生成箱号成功!');	
								        	  Ext.getCmp("pa_code").setValue(data.pa_code);
					                          Ext.getCmp("pa_restqty").setValue(data['pa_totalqty']);
					                          Ext.getCmp("pa_totalqty").setValue(data['pa_totalqty']);
					                          Ext.getCmp('win').close();
								        }
								    }				    			
							  });   				       										
						 }
					 },{
						 text:$I18N.common.button.erpCancelButton,
						 handler:function(){
							 Ext.getCmp('win').close();
						 }
					 }]
				}).show();   		  			
    }
});