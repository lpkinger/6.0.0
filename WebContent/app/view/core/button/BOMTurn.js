/**
 *bom类型转转 bo_style,bo_level
 * */
Ext.define('erp.view.core.button.BOMTurn',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBomTurnButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text:'更新BOM等级',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
        handler:function(){
        	var style = Ext.getCmp('bo_style'),
        	    level = Ext.getCmp('bo_level'), 
        		bg = 'background:#fffac0;color:#515151;';
        		if(!style){
                    style = {};
        			style.allowBlank = true;
        		}
        	 Ext.create('Ext.window.Window',{
            	 width:300,
            	 height:165,
            	 id:'win',
            	 title:'<h1>更新BOM等级</h1>',
            	 items:[{
            		 xtype:'dbfindtrigger',
            		 fieldLabel:'BOM类型',
            	     name:'bostyle',
            	     editable:true,
            	     id:'bostyle',
            	     fieldStyle: style.allowBlank ? '' : bg
            	 },{
                    xtype:'dbfindtrigger',
                    fieldLabel:'BOM等级',
                    name:'bolevel',
                    fieldStyle: level.allowBlank ? '' : bg,
                    id:'bolevel'
                 },{
                	 xtype:'checkbox',
                	 fieldLabel:'重置流程',
                	 name:'isprocess',
                	 id:'isprocess'
                 }],
            	 buttonAlign:'center',
            	 buttons:[{
      				xtype:'button',
      				columnWidth:0.12,
      				text:'保存',
      				width:60,
      				iconCls: 'x-button-icon-save',
      				handler:function(btn){
      					var bostyle=Ext.getCmp('bostyle').getValue();
      					var bolevel=Ext.getCmp('bolevel').getValue();
      					var isprocess=Ext.getCmp('isprocess').getValue();
      					var boid=Ext.getCmp('bo_id').getValue();
      					if(!style.allowBlank && !bostyle){
      				    	showError('请先设置相应BOM类型!');
      				    	return;
      				    }else if(!level.allowBlank && !bolevel){
      				    	showError('请先设置相应BOM等级!');
      				    	return;
      				    } else{
      				    	var dd=new Object();
      				    	dd['bolevel']=bolevel || '';
      				    	dd['boid']=boid;
      				    	dd['bostyle']=bostyle || '';
      				    	dd['isprocess']=isprocess?1:0;
      					   Ext.Ajax.request({
      					   	  url : basePath +'pm/bom/turnBOM.action',
      					   	  params :{
      					   		  _noc:1,
      					   		  data:unescape(Ext.JSON.encode(dd))
      					   		  },
      					   	  method : 'post',
      					   	  callback : function(options,success,response){
      					   		var localJson = new Ext.decode(response.responseText);
      					   		if(localJson.success){
      			    				Ext.Msg.alert('提示','更新成功!',function(){
      			    					Ext.getCmp('win').close();
      			    					window.location.reload();
      			    				});
      				   			} else if(localJson.exceptionInfo){
      				   				var str = localJson.exceptionInfo;
      				   					showError(str);
      					   				return;    					   			
      					   	 } else{
      				   				saveFailure();
      				   			}
      					   	  }
      					   });
      					   
      				   }
      				}
      			},{
      				xtype:'button',
      				columnWidth:0.1,
      				text:'关闭',
      				width:60,
      				iconCls: 'x-button-icon-close',
      				margin:'0 0 0 10',
      				handler:function(btn){
      					Ext.getCmp('win').close();
      				}
      			}]
             }).show();
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}        
	});