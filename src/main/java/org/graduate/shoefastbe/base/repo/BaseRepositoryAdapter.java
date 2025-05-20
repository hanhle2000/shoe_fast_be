package org.graduate.shoefastbe.base.repo;//package fa.training.HN24_CPL_JAVA_01_G3.base;
//
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//
//@AllArgsConstructor
//@Component
//public class BaseRepositoryAdapter<T extends BaseEntity> implements TargetRepository<T> {
//    private final BaseRepository<T> jpaRepository;
//
//    @Override
//    public <S extends T> S save(S entity) {
//        return jpaRepository.save(entity);
//    }
//
//    @Override
//    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
//        return jpaRepository.saveAll(entities);
//    }
//
//    @Override
//    public Optional<T> findById(Long id) {
//        return jpaRepository.findById(id);
//    }
//
//    @Override
//    public boolean existsById(Long id) {
//        return jpaRepository.existsById(id);
//    }
//
//    @Override
//    public Iterable<T> findAll() {
//        return jpaRepository.findAll();
//    }
//
//    @Override
//    public Iterable<T> findAllById(Iterable<Long> ids) {
//        return jpaRepository.findAllById(ids);
//    }
//
//    @Override
//    public void deleteById(Long id) {
//        jpaRepository.deleteById(id);
//    }
//
//    @Override
//    public void delete(T entity) {
//        jpaRepository.delete(entity);
//    }
//
//    @Override
//    public void deleteAllById(Iterable<? extends Long> ids) {
//        jpaRepository.deleteAllById(ids);
//    }
//
//    @Override
//    public void deleteAll(Iterable<? extends T> entities) {
//        jpaRepository.deleteAll();
//    }
//
//    @Override
//    public void deleteAll() {
//        jpaRepository.deleteAll();
//    }
//}
