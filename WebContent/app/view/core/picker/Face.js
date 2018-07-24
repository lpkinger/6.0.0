/**
 * 选择表情picker
 */
Ext.define('erp.view.core.picker.Face', {
	extend: 'Ext.Component',
    requires: 'Ext.XTemplate',
    alias: 'widget.facepicker',
    componentCls: 'x-face-picker',
    selectedCls: 'x-face-picker-selected',
    itemCls: 'x-face-picker-item',
    style: 'background: #f1f1f1;',
    facepath: basePath + 'resource/images/face/',
    format: '.gif',
    value: null,
    clickEvent: 'click',
    width: 230,
    height: 200,
    cols: 8,//8列
    initComponent : function(){
    	 var me = this;
         me.callParent(arguments);
         me.addEvents({
        	 'select': true 
         });
         if(me.handler){
        	 me.on('select', me.handler, me.scope, true);
         }
    },
    faces: [
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20',
            '21', '22', '23', '24', '25', '26', '27', '28', '29', '30', '31', '32', '33', '34', '35', '36', '37', '38', '39',
            '40', '41', '42', '43', '44', '45', '46', '47', '48'
	],
    onRender: function(container, position){
        var me = this,clickEvent = me.clickEvent;
        Ext.apply(me.renderData, {
            itemCls: me.itemCls,
            faces: me.faces
        });
        me.callParent(arguments);
        me.mon(me.el, clickEvent, me.handleClick, me, {delegate: 'a'});
        if(clickEvent != 'click'){
            me.mon(me.el, 'click', Ext.emptyFn, me, {delegate: 'a', stopEvent: true});
        }
    },
    afterRender : function(){
    	var me = this;
    	me.callParent(arguments);
    },
    renderTpl: [
                '<tpl for="faces">',
                	'<a hidefocus="on">',
                		'<div class="x-face-picker-item">',
                			'<img src="' + basePath + 'resource/images/face/{.}.gif" title="&f{.};">',
                		'</div>',
                    '</a>',
                '</tpl>'
    ],
	handleClick : function(event, target){
        var me = this;
        if(!me.disabled){
        	me.select(target.getElementsByTagName('img')[0].title);
        }
    },
	select: function(face, suppressEvent){
		 var me = this;
		 me.value = face;
		 me.fireEvent('select', me, face);
		 return;
	},
	getValue: function(){
		return this.value || null;
	}
});