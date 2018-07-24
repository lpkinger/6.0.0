
Ext.namespace("Ext.lingo");

Ext.lingo.GlossyPanel = Ext.extend(Ext.Panel, {
    collapsed: true,

    setupGlossy: function() {
        for (var i = 0; i < this.glossy.length; i++) {
            var item = this.glossy[i];
            var id = item.id;
            var el = Ext.get(id);
            cvi_glossy.add(el.dom, {color:'#4682B4', noshadow: true});
            el = Ext.get(id);
            el.hover(
                function(){
                    cvi_glossy.modify(this, {highlight: true})
                },
                function(){
                    cvi_glossy.modify(this, {highlight: false});
                },
                el.dom
            );
            el.on('mousedown', function(){
                cvi_glossy.modify(this, {pressed: true});
            }, el.dom);
            var fn = this.domMouseUp.createDelegate(this, [el.dom]);
            el.on('mouseup', fn);
            Ext.get('a_' + id).on('click', fn);
        }
    },

    domMouseUp: function(dom) {
        cvi_glossy.modify(dom, {pressed: false});
        var id = dom.id;
        for (var i = 0; i < this.glossy.length; i++) {
            var item = this.glossy[i];
            if (item.id == id) {
                App.addContent(item.name, item.title, item.url);
                break;
            }
        }
    },

    initComponent: function() {
        if (this.glossy) {
            var html = "";
            for (var i = 0; i < this.glossy.length; i++) {
                var item = this.glossy[i];
                html += '<img id="' + item.id + '" src="' + item.img + '" class="glossy" height="52" width="52" style="cursor:pointer"/><br />' +
                    '<span id="a_' + item.id + '"  style="font-weight:bold;cursor:pointer;">' +
                    item.title + '</span><br /><br />';
            }
            this.html = html;
            this.on('expand', this.setupGlossy, this);
        }
        Ext.lingo.GlossyPanel.superclass.initComponent.call(this);
    }
});
