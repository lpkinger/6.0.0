Ext.define('erp.view.oa.knowledge.KnowledgeMapLink',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				 defaults: {
                split: true
                },
				items: [{
					xtype: 'erpFormPanel',
					items:[	
					{
						columnWidth:'0.08',
						html:'<div id="sidebar"><a href="javascript:Recommend();"  style="text-decoration: none||blink;font-size:14px;text-align:left;font-weight: bold; ">知识推荐</a></div>',
					},{
						columnWidth:'0.08',
						html:'<div id="sidebar"><a href="javascript:Version();"  style="text-decoration: none||blink;font-size:14px; font-weight: bold;">历史版本</a></div>',
					},{
						columnWidth:'0.08',
						html:'<div id="sidebar"><a href="javascript:Commont();"  style="text-decoration: none||blink;font-size:14px; font-weight: bold;">添加评论</a></div>',
					},{
					  columnWidth:'0.26',
					  html:'<div></div>',
					},{
					  text:'知识详情',
					  fieldLabel:'知识详情',
					  id:'knowledgedetails',
					  labelStyle:'font-size:14px;font-weight: bold;',
					  columnWidth:'0.5',
					  readOnly:true,
	                  fieldStyle : 'background:#f0f0f0;border-bottom-style: 1px solid #8B8970;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;color:#CD661D;border-bottom-style:1px solid;border-left:none; ',
					}],
					setReadOnly: function(bool){
						this.readOnly = bool;
					},
					anchor: '100% 60%',
					saveUrl: 'oa/knowledge/saveKnowledge.action',
					deleteUrl:'oa/knowledge/deleteKnowledge.action',
					updateUrl:'oa/knowledge/updateKnowledge.action',
					getIdUrl: 'common/getId.action?seq=KNOWLEDGE_SEQ',
					keyField: 'kl_id',
					codeField:'kl_code',
				},{
				   anchor: '100% 40%',
				   region:'south',
                   split: true,
                   layout:'border',
                   items: [{
                   title:'<font color=#1C86EE;>知识点评</font>',
                   region: 'center',
                   caller:'KnowledgeComment',
                   xtype:'erpKnowledgeMapLinkGrid',
                   flex:1,
                   split:true,
                 }, {
                   title: '<font color=#1C86EE; >相关知识</font>',
                   region: 'east',
                   caller:'KnowledgeMapping',
                   xtype:'erpknowledgemappinggrid',
                   width:'85%', 
                   split: true,
				}]
			}
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});