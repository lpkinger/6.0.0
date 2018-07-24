Ext.define('erp.view.pm.mes.OverStationGet',{ 
	extend: 'Ext.Viewport', 
    layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				id:'form',
				title:'过站采集',
				xtype: 'form',
				anchor: '100% 35%',
				bodyStyle: 'background: #f1f1f1;',
				bodyPadding:5,
				autoScroll: true,
				scrollable: true,
				items:[{
					xtype: 'fieldcontainer',					
					defaults: {
						width: 250,
						fieldStyle : 'background:#f0f0f0;border: 0px solid #8B8970;font-color:blue'
					}, 
					layout: {
						type: 'table',
						columns: 4
					},
					items: [{
							xtype: 'textfield',
							fieldLabel: '资源编号',
							allowBlank:false,
							id:'sc_code',
							name:'sc_code',
							readOnly:true							
						},{
							xtype: 'textfield',
							fieldLabel: '资源名称',
							readOnly:true,						
							id:'sc_name',
							name:'sc_name'
						},{
							xtype: 'textfield',
							fieldLabel: '工序编号',
							readOnly:true,
							id:'st_code',
							name:'st_code'
						},{
							xtype: 'textfield',
							fieldLabel: '工序名称',
							readOnly:true,
							id:'st_name',
							name:'st_name'
						},{
							xtype: 'textfield',
							fieldLabel: '作业单号',
							id:'mc_code' ,
							name:'mc_code',
							readOnly:true
						},{			
						    xtype: 'textfield',
							fieldLabel: '工单数量',
							readOnly:true,
							id:'mc_qty',
							name:'mc_qty'
						},{
						    xtype: 'textfield',
							fieldLabel: '计数',
							readOnly : true,
							id:'mcd_inqty'
						},{
						    xtype: 'textfield',
							fieldLabel: '剩余',
							readOnly : true,
							id:'mcd_restqty'
				       },{
				       	    xtype: 'checkbox',
		                    boxLabel  : '拼板采集',
		                    name      : 'getCombine',
		                    checked   : false,
		                    id        : 'getCombine',
		                    fieldStyle:''
		               },{
				            xtype: 'textfield',
							fieldLabel: '序列号',
							id:'sn_code',
							name:'sn_code',
							colspan:2,
							fieldStyle : "background:rgb(224, 224, 255);",    
				            labelStyle:"color:red;",
				            emptyText:'请录入序列号'
				       }]			 
			    }]
			},{
				xtype: 'dataview',
				anchor: '100% 50%',
				id: 't_result',
				autoScroll: true,
				scrollable: true,
				store: new Ext.data.Store({
					fields: ['type', 'text']
				}),
				cls: 'msg-body',
				tpl: new Ext.XTemplate(
				    '<audio id="audio-success" src="' + basePath + 'resource/audio/success.wav"></audio>',
				    '<audio id="audio-error" src="' + basePath + 'resource/audio/error.wav"></audio>',
				    '<tpl for=".">',
				         '<div class="msg-item">',
				            '<tpl if="type == \'success\'"><span class="text-info">{text}</span></tpl>',
				            '<tpl if="type == \'error\'"><span class="text-warning">{text}</span></tpl>',
				          '</div>',
				    '</tpl>'
				),
			   itemSelector: 'div.msg-item',
			   emptyText: '提示信息',
			   deferEmptyText: false,
			   autoScroll: true,
			   append: function(text, type) {
				    type = type || 'success';
				    this.getStore().add({text: text, type: type});
				    this.getEl().scroll("b", this.getEl().getHeight(), true);  
				    var el = Ext.get('audio-' + type).dom;
				    el.play();
				}
		    }] 
		}); 
		me.callParent(arguments); 
	} 
});