package com.example.jangbogovrp.model;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class Migration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        // 버전 1로 마이그레이션: 클래스를 생성합니다
        if (oldVersion == 0) {
            schema.create("Person")
                    .addField("name", String.class)
                    .addField("age", int.class);
            oldVersion++;
        }

        // 버전 2로 마이그레이션: 기본 키를 넣고 객체를 참조합니다
        if (oldVersion == 1) {
            schema.get("Person")
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addRealmObjectField("favoriteDog", schema.get("Dog"))
                    .addRealmListField("dogs", schema.get("Dog"));
            oldVersion++;
        }
    }
}
