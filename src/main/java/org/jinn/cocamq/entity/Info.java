package org.jinn.cocamq.entity;

/**
 * Created by gumingcn on 14-7-28.
 */
public class Info {
        private Long user_id=0l;
        private Long cart_id=0l;
        private Long cart_record_id=0l;
        private Long size_id=0l;
        private Long brand_id=0l;
        private Long merchandise_id=0l;
        private Long num=0l;
        private String channel;
        private String warehouse;

        public String getWarehouse() {
            return warehouse;
        }

        public void setWarehouse(String warehouse) {
            this.warehouse = warehouse;
        }

        public Long getUser_id() {
            return user_id;
        }

        public void setUser_id(Long user_id) {
            this.user_id = user_id;
        }

        public Long getCart_id() {
            return cart_id;
        }

        public void setCart_id(Long cart_id) {
            this.cart_id = cart_id;
        }

        public Long getCart_record_id() {
            return cart_record_id;
        }

        public void setCart_record_id(Long cart_record_id) {
            this.cart_record_id = cart_record_id;
        }

        public Long getSize_id() {
            return size_id;
        }

        public void setSize_id(Long size_id) {
            this.size_id = size_id;
        }

        public Long getBrand_id() {
            return brand_id;
        }

        public void setBrand_id(Long brand_id) {
            this.brand_id = brand_id;
        }

        public Long getMerchandise_id() {
            return merchandise_id;
        }

        public void setMerchandise_id(Long merchandise_id) {
            this.merchandise_id = merchandise_id;
        }

        public Long getNum() {
            return num;
        }

        public void setNum(Long num) {
            this.num = num;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        @Override
        public String toString() {
            return "Info{" +
                    "user_id=" + user_id +
                    ", cart_id=" + cart_id +
                    ", cart_record_id=" + cart_record_id +
                    ", size_id=" + size_id +
                    ", brand_id=" + brand_id +
                    ", merchandise_id=" + merchandise_id +
                    ", num=" + num +
                    ", channel='" + channel + '\'' +
                    ", warehouse='" + warehouse + '\'' +
                    '}';
        }

}
